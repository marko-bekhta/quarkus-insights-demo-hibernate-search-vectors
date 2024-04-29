import {LitElement, html, css, unsafeCSS} from 'lit';
import {customElement, property, state, queryAll} from 'lit/decorators.js';
import './book-item'
import {BOOK_SEARCH_NEXT_PAGE_EVENT, BOOK_SEARCH_RESULT_EVENT, BOOK_SEARCH_START_EVENT, BookSearchResult} from "./book-form";
import debounce from 'lodash/debounce';
import icons from "./icons";


/**
 * This component is the target of the search results
 */
@customElement('book-target')
export class BookTarget extends LitElement {

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
      grid-template-columns: repeat(1, 1fr);
      grid-gap: 1em;
      clear: both;
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
   
  `;

  @property({type: String}) private type: string = "guide";
  @state() private _result: BookSearchResult | undefined;
  @state() private _loading = false;
  @queryAll('.book-hit') private _hits: NodeListOf<HTMLElement>;

  private _form: HTMLElement;

  connectedCallback() {
    super.connectedCallback();
    this._form = document.querySelector("book-form");
    this._form.addEventListener(BOOK_SEARCH_RESULT_EVENT, this._handleResult);
    this._form.addEventListener(BOOK_SEARCH_START_EVENT, this._loadingStart);
    document.addEventListener('scroll', this._handleScrollDebounced)
  }

  disconnectedCallback() {
    this._form.removeEventListener(BOOK_SEARCH_RESULT_EVENT, this._handleResult);
    this._form.removeEventListener(BOOK_SEARCH_START_EVENT, this._loadingStart);
    document.removeEventListener('scroll', this._handleScrollDebounced);
    super.disconnectedCallback();
  }

  render() {
    if (this._result?.hits) {
      if (this._result.hits.length === 0) {
        return html`
          <div id="book-target" class="no-hits">
            <p>Sorry, nothing matched your search. Please try again.</p>
          </div>
        `;
      }
      const result = this._result.hits.map(i => this._renderHit(i));
      return html`
        <div id="book-target" class="book-hits" aria-label="Search Hits">
          ${result}
        </div>
        ${this._loading ? this._renderLoading() : ''}
      `;
    }
    if (this._loading) {
      return html`
        <div id="book-target">${this._renderLoading()}</div>`;
    }
    return html`
      <div id="book-target">
        <slot></slot>
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
          <book-guide class="book-hit" .data=${i}></book-guide>`
    }
    return ''
  }


  private _handleScroll = (e) => {
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
    const scrollElement = document.documentElement // Scroll bar is on the <html> element
    const bottomOfViewport = scrollElement.scrollTop + scrollElement.clientHeight
    const topOfLastResultCard = lastHit.offsetTop
    if (bottomOfViewport >= topOfLastResultCard) {
      // We have scrolled to the bottom of the last result card.
      this._loading = true;
      this._form.dispatchEvent(new CustomEvent(BOOK_SEARCH_NEXT_PAGE_EVENT));
    }
  }
  private _handleScrollDebounced = debounce(this._handleScroll, 100);

  private _handleResult = (e: CustomEvent) => {
    console.debug("Received results in book-target: ", e.detail);
    this._loadingEnd();
    if (!this._result || !e.detail || !e.detail.hits || e.detail.page === 0) {
      if(e.detail?.hits) {
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

  private _loadingStart = (e:CustomEvent) => {
    this._loading = true;
    if(e.detail.page === 0) {
      this._result = undefined;
    }
  }

  private _loadingEnd = () => {
    this._loading = false;
  }
}