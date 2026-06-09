
async function loadIndex() {
    const response = await fetch(buildUrl(`/${getLang()}/search_index.json`));
    return response.json();
}

function getQuery() {
    const params = new URLSearchParams(window.location.search);
    return params.get('q') || '';
}

const LOCALE_PATTERN = /^[a-z]{2}_[a-z]{2}$/i;
const DEFAULT_LANG = 'en_us';

function getPathInfo() {
    const pathParts = window.location.pathname.split('/').filter(Boolean);
    const localeIndex = pathParts.findIndex((part) => LOCALE_PATTERN.test(part));
    if (localeIndex === -1) {
        // Unlikely in normal use (all content pages live under /{base}/{locale}/).
        // Fall back to en_us and infer a single-segment site prefix when present.
        const sitePrefix = pathParts.length === 1 && !pathParts[0].includes('.')
            ? '/' + pathParts[0]
            : '';
        return { baseUrl: sitePrefix, lang: DEFAULT_LANG };
    }
    const lang = pathParts[localeIndex];
    const baseUrl = localeIndex > 0
        ? '/' + pathParts.slice(0, localeIndex).join('/')
        : '';
    return { baseUrl, lang };
}

function getLang() {
    return getPathInfo().lang;
}

function getBaseUrl() {
    return getPathInfo().baseUrl;
}

function buildUrl(path) {
    const baseUrl = getBaseUrl();
    return baseUrl ? `${baseUrl}${path}` : path;
}

function boldQuery(text, query) {
    if (!query) return text;
    const regex = new RegExp(`(${query})`, 'gi');
    return text.replace(regex, '<b>$1</b>');
}

async function performSearch(query) {
    const data = await loadIndex();
    const fuse = new Fuse(data, {
        keys: ['content'],
        includeScore: true,
        threshold: 0.1,          // higher = more lenient
        ignoreLocation: true,    // disable position penalty
        minMatchCharLength: 2,   // ignore single-letter queries
    });

    let results = fuse.search(query);
    const seenUrls = new Set();
    results = results.filter(r => {
        if (seenUrls.has(r.item.url)) {
            return false;
        }
        seenUrls.add(r.item.url);
        return true;
    });
    const resultsList = document.getElementById('results');
    resultsList.innerHTML = results
    .map(r => `<li><a href="${r.item.url}">${r.item.entry}</a><p>${boldQuery(r.item.content, query)}</p></li>`)
    .join('') || '<li>No results found.</li>';
}

const input = document.getElementById('search-box');
const query = getQuery();

input.value = query;
if (query) performSearch(query);

document.getElementById('search-box').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
      const query = encodeURIComponent(this.value.trim());
      if (!query) return;

      window.location.href = buildUrl(`/${getLang()}/search.html?q=${query}`);
    }
});

function handleSearch(e) {
    e.preventDefault(); // Prevent page reload
    const query = document.getElementById('search-box').value.trim();
    if (!query) return;
    window.location.href = buildUrl(`/${getLang()}/search.html?q=${query}`);
}