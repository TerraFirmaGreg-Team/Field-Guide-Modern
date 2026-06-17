(function () {
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

  function setGiscusTheme(theme) {
    document
      .querySelector('#giscus-container iframe.giscus-frame')
      ?.contentWindow?.postMessage({ giscus: { setConfig: { theme } } }, 'https://giscus.app')
  }

  function mountGiscus(container) {
    container.replaceChildren()
    const script = document.createElement('script')
    script.src = 'https://giscus.app/client.js'
    script.setAttribute('data-repo', GISCUS.repo)
    script.setAttribute('data-repo-id', GISCUS.repoId)
    script.setAttribute('data-category', GISCUS.category)
    script.setAttribute('data-category-id', GISCUS.categoryId)
    script.setAttribute('data-mapping', 'pathname')
    script.setAttribute('data-theme', giscusTheme())
    script.setAttribute('data-lang', giscusLang(document.getElementById('comments')?.dataset.giscusLang))
    script.setAttribute('data-loading', 'lazy')
    script.crossOrigin = 'anonymous'
    script.async = true
    container.appendChild(script)
  }

  async function init() {
    const section = document.getElementById('comments')
    const container = document.getElementById('giscus-container')
    if (!section || !container) return

    try {
      const res = await fetch('/giscus-config.json')
      if (res.ok) {
        const json = await res.json()
        if (json.enabled === false) return
        Object.assign(GISCUS, json)
      }
    } catch {
    }

    section.hidden = false
    mountGiscus(container)
    window.addEventListener('handbook-theme-change', (event) => {
      const theme = event.detail?.theme === 'dark' ? 'dark' : 'light'
      setGiscusTheme(theme)
    })
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init)
  } else {
    void init()
  }
})()
