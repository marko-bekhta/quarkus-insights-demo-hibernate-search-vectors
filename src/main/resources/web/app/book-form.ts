import {css, html, LitElement} from 'lit-element';
import {customElement, property, state} from 'lit/decorators.js';
import debounce from 'lodash/debounce';

export const BOOK_SEARCH_START_EVENT = 'book-start';
export const BOOK_SEARCH_RESULT_EVENT = 'book-result';
export const BOOK_SEARCH_NEXT_PAGE_EVENT = 'book-next-page';
export const BOOK_SEARCH_FIND_SIMILAR_EVENT = 'book-find-similar';

export interface BookSearchResult {
    hits: BookSearchHit[];
    hasMoreHits: boolean;
}

export interface BookSearchHit {
    title: string;
    summary: string;
    url: string;
    keywords: string | undefined;
    content: string | undefined;
    type: string | undefined;
}

@customElement('book-form')
export class BookForm extends LitElement {

    static styles = css`
        .search-query {
            display: flex;
            flex-flow: row;
            gap: 10px;
        }

        select, input {
            padding: 10px;
            border-radius: 5px;
            outline: none;
            border: 1px solid;
            width: 100%;
        }
    `;

    @property({type: String}) server: string = "";
    @property({type: String, attribute: 'min-chars'}) minChars: number = 3;

    @state({
        hasChanged(newVal: any, oldVal: any) {
            return JSON.stringify(newVal) !== JSON.stringify(oldVal);
        }
    })
    private _formData: any;
    @state()
    private _genres?: string[] = [];
    private _requestType: "similar" | "simple";

    private _page: number = 0;
    private _currentHitCount: number = 0;
    private _abortController?: AbortController = null;

    constructor() {
        super();
        this._jsonFetchCategories();
        this._search();
    }

    render() {
        return html`
            <div id="book-form">
                <div class="search-query">
                    <input name="q" type="text" aria-label="Search for Books" placeholder="What book do you like?"
                           @keyup=${this._handleInputChange}
                           @change=${this._handleInputChange}
                    />
                    <select name="genre" @change=${this._handleSelectChange}>
                        <option>Pick a genre</option>
                        ${this._genres.map((genre) =>
                                html`
                                    <option value="genre">${genre}</li>`
                        )}
                    </select>
                </div>
            </div>
        `;
    }

    update(changedProperties: Map<any, any>) {
        if (!this._formData) {
            this._clearSearch();
        } else {
            this._searchDebounced();
        }
        return super.update(changedProperties);
    }

    connectedCallback() {
        super.connectedCallback();
        this.addEventListener(BOOK_SEARCH_NEXT_PAGE_EVENT, this._handleNextPage);
        this.addEventListener(BOOK_SEARCH_FIND_SIMILAR_EVENT, this._handleFindSimilar);
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        this.removeEventListener(BOOK_SEARCH_NEXT_PAGE_EVENT, this._handleNextPage);
        this.removeEventListener(BOOK_SEARCH_FIND_SIMILAR_EVENT, this._handleFindSimilar);
    }

    private _search = () => {
        if (this._abortController) {
            // If a search is already in progress, abort it
            this._abortController.abort();
        }
        if (!this._formData) {
            this._clearSearch();
            return;
        }
        const controller = new AbortController();
        this._abortController = controller;
        this.dispatchEvent(new CustomEvent(BOOK_SEARCH_START_EVENT, {detail: {page: this._page}}));

        this._jsonFetch(controller, 'GET', this._formData)
            .then((r: any) => {
                if (this._page > 0) {
                    this._currentHitCount += r.hits.length;
                } else {
                    this._currentHitCount = r.hits.length;
                }
                const total = r.total;
                const hasMoreHits = r.hits.length > 0 && total > this._currentHitCount;
                this.dispatchEvent(new CustomEvent(BOOK_SEARCH_RESULT_EVENT, {
                    detail: {
                        ...r,
                        search: this._formData,
                        page: this._page,
                        hasMoreHits
                    }
                }));
            }).catch(e => {
            console.error('Could not run search: ' + e);
            if (this._abortController != controller) {
                // A concurrent search erased ours; most likely input changed while waiting for results.
                // Ignore this search and let the concurrent one reset the data as it sees fit.
                return;
            }
            this._page = 0;
            this._currentHitCount = 0;
        }).finally(() => {
            if (this._abortController == controller) {
                this._abortController = null
            }
        });
    }


    private _searchDebounced = debounce(this._search, 300);

    private _handleInputChange = (e: Event) => {
        this._requestType = 'simple';
        const target = e.target as HTMLFormElement;
        const q = target.value.length === 0 || target.value.length < this.minChars ? '' : target.value;
        this._formData = {...this._formData, q: q};
    }

    private _handleSelectChange = (e: Event) => {
        this._requestType = 'simple';
        const target = e.target as HTMLFormElement;
        const q = this._formData.q;
        if (target.selectedIndex > 0) {
            this._formData = {q: q, genres: target.options[target.selectedIndex].text};
        } else {
            this._formData = {q: q};
        }
    }

    private _handleNextPage = (e: CustomEvent) => {
        this._page++;
        this._search();
    }

    private _handleFindSimilar(e: CustomEvent) {
        this._clearSearch();
        this._requestType = 'similar';
        this._formData = {
            book: e.detail.book
        };
    }

    private _isInput(el: HTMLFormElement) {
        return el.tagName.toLowerCase() === 'input'
    }

    private async _jsonFetch(controller: AbortController, method: string, params: object) {
        const queryParams: Record<string, string> = {
            ...params,
            'page': this._page.toString()
        };
        const timeoutId = setTimeout(() => controller.abort(), 1000)
        const apiPath = this._requestType === 'simple'
            ? `${this.server}/api/books?${(new URLSearchParams(queryParams)).toString()}`
            : `${this.server}/api/books/${this._formData.book}/similar?${(new URLSearchParams(queryParams)).toString()}`;
        const response = await fetch(apiPath, {
            method: method,
            signal: controller.signal,
            body: null
        })
        clearTimeout(timeoutId)
        if (response.ok) {
            return await response.json()
        } else {
            throw 'Response status is ' + response.status + '; response: ' + await response.text()
        }
    }

    private async _jsonFetchCategories() {
        const response = await fetch(`${this.server}/api/genres`, {
            method: 'GET',
            body: null
        });

        if (response.ok) {
            this._genres = await response.json();
        } else {
            throw 'Response status is ' + response.status + '; response: ' + await response.text()
        }
    }

    private _clearSearch() {
        this._page = 0;
        this._currentHitCount = 0;
        this._requestType = 'simple';
        if (this._abortController) {
            this._abortController.abort();
            this._abortController = null;
        }

        this._formData = {}

        this.dispatchEvent(new CustomEvent(BOOK_SEARCH_RESULT_EVENT));
    }

}
