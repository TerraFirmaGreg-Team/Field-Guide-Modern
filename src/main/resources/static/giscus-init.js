(() => {
  'use strict'

  const GISCUS_HOST = 'https://giscus.app'

  const GISCUS = {
    repo: 'TerraFirmaGreg-Team/Modpack-Modern',
    repoId: 'R_kgDOH_FIbA',
    category: 'General',
    categoryId: 'DIC_kwDOH_FIbM4CbMDm',
  }

  const GISCUS_LANG = {
    en_us: 'en',
    zh_cn: 'zh-CN',
    zh_tw: 'zh-TW',
    zh_hk: 'zh-TW',
    ja_jp: 'ja',
    ko_kr: 'ko',
    fr_fr: 'fr',
    de_de: 'de',
    es_es: 'es',
    ru_ru: 'ru',
    uk_ua: 'ru',
    pl_pl: 'pl',
    pt_br: 'pt',
    tr_tr: 'tr',
    sv_se: 'sv',
    hu_hu: 'hu',
  }

  function giscusLang(locale) {
    const key = String(locale || '').trim().toLowerCase().replace(/-/g, '_')
    return GISCUS_LANG[key] || 'en'
  }

  function giscusTheme() {
    return document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'dark' : 'light'
  }

  function sendGiscusConfig(message) {
    const iframe = document.querySelector('#giscus-container iframe')
    if (!iframe?.contentWindow) return false
    iframe.contentWindow.postMessage({ giscus: message }, GISCUS_HOST)
    return true
  }

  function applyTheme() {
    const theme = giscusTheme()
    if (sendGiscusConfig({ setConfig: { theme } })) return

    const container = document.getElementById('giscus-container')
    if (!container) return

    const observer = new MutationObserver(() => {
      if (sendGiscusConfig({ setConfig: { theme: giscusTheme() } })) {
        observer.disconnect()
      }
    })
    observer.observe(container, { childList: true, subtree: true })
  }

  function mountGiscus(section) {
    const container = document.getElementById('giscus-container')
    if (!container) return

    const script = document.createElement('script')
    script.src = `${GISCUS_HOST}/client.js`
    script.setAttribute('data-repo', GISCUS.repo)
    script.setAttribute('data-repo-id', GISCUS.repoId)
    script.setAttribute('data-category', GISCUS.category)
    script.setAttribute('data-category-id', GISCUS.categoryId)
    script.setAttribute('data-mapping', 'pathname')
    script.setAttribute('data-theme', giscusTheme())
    script.setAttribute('data-lang', giscusLang(section.dataset.giscusLang))
    script.setAttribute('data-reactions-enabled', '1')
    script.setAttribute('data-emit-metadata', '0')
    script.setAttribute('data-input-position', 'bottom')
    script.setAttribute('data-loading', 'lazy')
    script.crossOrigin = 'anonymous'
    script.async = true
    container.replaceChildren(script)
  }

  function init() {
    const section = document.getElementById('comments')
    if (!section) return

    mountGiscus(section)
    section.hidden = false

    window.addEventListener('handbook-theme-change', applyTheme)
    new MutationObserver(applyTheme).observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['data-bs-theme'],
    })
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init)
  } else {
    init()
  }
})()
