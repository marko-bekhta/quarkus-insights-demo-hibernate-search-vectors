import {css, html, LitElement, unsafeCSS} from 'lit';
import {customElement, property, queryAll, state} from 'lit/decorators.js';
import './book-item'
import {
    BOOK_SEARCH_FIND_SIMILAR_EVENT, BOOK_SEARCH_SIMILAR_NEXT_EVENT,
    BOOK_SEARCH_SIMILAR_RESULT_EVENT,
    BOOK_SEARCH_START_EVENT,
    BookSearchResult
} from "./book-form";
import icons from "./icons";


/**
 * This component is the target of the search results
 */
@customElement('book-similar')
export class BookSimilar extends LitElement {

    static styles = css`

        .loading {
            background-image: url('${unsafeCSS(icons.loading)}');
            background-repeat: no-repeat;
            background-position: top;
            background-size: 45px;
            padding-top: 55px;
            text-align: center;
            padding-bottom: 70px;
        }

        .book-hits {
            display: grid;
            grid-auto-rows: 1fr;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            align-items: stretch;
            grid-gap: 1em;
            clear: both;
            overflow-x: auto;
            grid-auto-flow: column;
        }

        .no-hits {
            padding: 10px;
            margin: 10px;
            font-size: 1.2rem;
            line-height: 1.5;
            font-weight: 400;
            font-style: italic;
            text-align: center;
            background: #F0C9AE;
        }


        book-guide {
            grid-column: span 4;
            margin: 1rem 0rem 1rem 0rem;

            @media screen and (max-width: 1300px) {
                grid-column: span 6;
            }

            @media screen and (max-width: 768px) {
                grid-column: span 12;
                margin: 1rem 0rem 1rem 0rem;
            }

            @media screen and (max-width: 480px) {
                grid-column: span 12;
            }
        }
        
        .similar-books-button {
            width: 55px;
            border-radius: 10px;
            background: #444;
            color: #fff;
            padding: 5px 20px 5px 20px;
            font-size: 0.7rem;
            cursor: pointer;
            margin-left: auto;
            margin-top: auto;

            &:hover {
                background: #999;
                color: #fff;
            }
        }
    }
    `;

    @property({type: String}) private type: string = "guide";
    @property({type: Number}) public bookId: number;
    @state() private _result: BookSearchResult | undefined;
    @state() private _loading = false;
    @queryAll('.book-hit') private _hits: NodeListOf<HTMLElement>;

    @property({type: String}) server: string = "";

    private _page: number = 0;
    private _currentHitCount: number = 0;
    private _abortController?: AbortController = null;

    constructor() {
        super();
    }

    connectedCallback() {
        super.connectedCallback();
        this.addEventListener(BOOK_SEARCH_SIMILAR_RESULT_EVENT, this._handleResult);
        this.addEventListener(BOOK_SEARCH_SIMILAR_NEXT_EVENT, this._handleNext);
        console.log(this.bookId);
        this._search();
    }

    disconnectedCallback() {
        this.removeEventListener(BOOK_SEARCH_SIMILAR_RESULT_EVENT, this._handleResult);
        this.removeEventListener(BOOK_SEARCH_SIMILAR_NEXT_EVENT, this._handleNext);
        super.disconnectedCallback();
    }

    render() {
        if (this._result?.hits) {
            if (this._result.hits.length === 0) {
                return html`
                    <div id="similar-books" class="no-hits">
                        <p>Sorry, no similar books found. Please try a different book.</p>
                    </div>
                `;
            }
            const result = this._result.hits.map(i => this._renderHit(i));
            return html`
                <div class="similar-books-button" @click="${this._handleNext}">
                    Find more
                </div>
                <div id="similar-books" class="book-hits" aria-label="Search Hits">
                    ${result}
                </div>
                ${this._loading ? this._renderLoading() : ''}
            `;
        }
        if (this._loading) {
            return html`
                <div id="similar-books">${this._renderLoading()}</div>`;
        }
        return html`
            <div>
            </div>
        `;
    }


    private _renderLoading() {
        return html`
            <div class="loading">Searching...</div>
        `;
    }

    private _renderHit(i) {
        switch (this.type) {
            case 'guide':
                return html`
                    <book-similar-item class="book-hit" .data=${i}></book-similar-item>`
        }
        return ''
    }

    private _search = () => {
        if (this._abortController) {
            // If a search is already in progress, abort it
            this._abortController.abort();
        }
        const controller = new AbortController();
        this._abortController = controller;
        this.dispatchEvent(new CustomEvent(BOOK_SEARCH_START_EVENT, {detail: {page: this._page}}));

        this._jsonFetch(controller, 'GET')
            .then((r: any) => {
                if (this._page > 0) {
                    this._currentHitCount += r.hits.length;
                } else {
                    this._currentHitCount = r.hits.length;
                }
                const total = r.total;
                const hasMoreHits = r.hits.length > 0 && total > this._currentHitCount;
                this.dispatchEvent(new CustomEvent(BOOK_SEARCH_SIMILAR_RESULT_EVENT, {
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


    private async _jsonFetch(controller: AbortController, method: string) {
        const queryParams: Record<string, string> = {
            'page': this._page.toString()
        };
        const timeoutId = setTimeout(() => controller.abort(), 1000)
        const apiPath = `${this.server}/api/books/${this.bookId}/similar?${(new URLSearchParams(queryParams)).toString()}`;
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


    private _handleNext = (e) => {
        if (this._loading) {
            return;
        }
        if (!this._result) {
            // No search.
            return;
        }
        if (!this._result.hasMoreHits) {
            // No more hits to fetch.
            console.log("no more hits");
            return
        }
        const lastHit = this._hits.length == 0 ? null : this._hits[this._hits.length - 1]
        if (!lastHit) {
            // No result card is being displayed at the moment.
            return
        }
        this._loading = true;
        this._page++;
        this._search();
    }

    private _handleResult = (e: CustomEvent) => {
        console.debug("Received results in book-target: ", e.detail);
        this._loadingEnd();
        if (!this._result || !e.detail || !e.detail.hits || e.detail.page === 0) {
            if (e.detail?.hits) {
                document.body.classList.add("book-has-results");
            } else {
                document.body.classList.remove("book-has-results");
            }
            this._result = e.detail;
            return;
        }
        this._result.hits = this._result.hits.concat(e.detail.hits);
        console.debug(`${this._result.hits.length} results in book-target: `);
        this._result.hasMoreHits = e.detail.hasMoreHits;
    }

    private _loadingStart = (e: CustomEvent) => {
        this._loading = true;
        if (e.detail.page === 0) {
            this._result = undefined;
        }
    }

    private _loadingEnd = () => {
        this._loading = false;
    }
}
