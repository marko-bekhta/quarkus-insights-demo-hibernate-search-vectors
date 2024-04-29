import {css, html, LitElement} from 'lit';
import {unsafeHTML} from 'lit/directives/unsafe-html.js';
import {customElement, property} from 'lit/decorators.js';
import {BOOK_SEARCH_FIND_SIMILAR_EVENT, BOOK_SEARCH_START_EVENT} from "./book-form";

/**
 * This component is a single guide hit in the search results
 */
@customElement('book-guide')
export class BookItem extends LitElement {

    static styles = css`
        :host {
            --link-color: #1259A5;
            --link-hover-color: #c00;
            --content-highlight-color: #777;
        }

        @media screen and (max-width: 1300px) {
            .book-hit {
                grid-column: span 6;
            }
        }

        .highlighted {
            font-weight: bold;
        }

        .book-guide {
            display: flex;
            column-gap: 20px;
        }

        .book-guide a {
            line-height: 1.5rem;
            font-weight: 400;
            cursor: pointer;
            text-decoration: underline;
            color: var(--link-color);
        }

        .book-guide a:hover {
            color: var(--link-hover-color);
        }

        .book-guide h3 {
            margin: 1rem 0 0 0;
            display: flex;
            align-content: center;

            span {
                align-content: center;
            }
            
            .similar-books-button {
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
        .book-guide h5 {
            margin: 0;
        }

        .book-guide div {
            margin: 1rem 0 0 0;
            font-size: 1rem;
            line-height: 1.5rem;
            font-weight: 400;
        }

        .book-guide .content-highlights {
            font-size: 0.7rem;
            line-height: 1rem;
            color: var(--content-highlight-color);

            p {
                margin: 0 0 .5rem;
            }
        }

        .book-guide-icon svg {
            width: 70px;
            margin: 1rem 0 0 0;
            fill: var(--main-text-color);
        }

        .summary {
            min-height: 40px;
        }

        .genres {
            display: flex;
            gap: 10px;

            p {
                border-radius: 10px;
                background: #555;
                color: #fff;
                padding: 5px 20px 5px 20px;
                font-size: 0.7rem;
                cursor: pointer;

                &:hover {
                    background: #999;
                    color: #fff;
                }
            }
        }

        .book-cover img {
            width: 200px;
        }
    `;

    @property({type: Object}) data: any;
    @property({type: String}) id: number;
    @property({type: String}) author: any;
    @property({type: String}) title: string;
    @property({type: String}) summary: string;
    @property({type: String}) coverLocation: string;
    @property({type: String}) genres: string | [string]

    private _form: HTMLElement;

    connectedCallback() {
        this._form = document.querySelector("book-form");
        if (this.data) {
            for (const key in this.data) {
                if (this.data.hasOwnProperty(key)) {
                    this[key] = this.data[key];
                }
            }
        }
        super.connectedCallback();
    }

    disconnectedCallback() {
        super.disconnectedCallback();
    }


    _handleFindSimilar(){
        console.log("aaa")
        this._form.dispatchEvent(new CustomEvent(BOOK_SEARCH_FIND_SIMILAR_EVENT, {detail: {book: this.id}}));
    }

    render() {
        return html`
            <div class="book-hit book-guide" aria-label="Book Hit">
                <div class="book-cover">
                    <img src="/api/image/${this.coverLocation}" alt="book cover"/>
                </div>
                <div>
                    <h3>
                        <span>${this._renderHTML(this.title)}</span>
                        <div data-id="${this.id}" class="similar-books-button" @click="${this._handleFindSimilar}">Find similar</div>
                    </h3>
                    <h5>${this.author?.name}</h5>
                    <div class="summary">
                        <p>${this._renderHTML(this.summary)}</p>
                    </div>
                    <div class="genres">${this._renderHTML(this.genres)}</div>
                </div>
            </div>
        `;
    }

    private _renderHTML(content?: string | [string]) {
        if (!content) {
            return content;
        }
        if (Array.isArray(content)) {
            return content.map((c) => html`<p>${this._renderHTML(c)}</p>`);
        }
        return unsafeHTML(content);
    }
}