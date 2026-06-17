(function () {
  const GITALK_ID_MAX = 49
  const CONFIG_URLS = ['/gitalk-config.json', 'https://wiki.terrafirmagreg.team/gitalk-config.json']

  const GITALK_LANGUAGE = {
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

  function gitalkLanguage(locale) {
    const key = String(locale || '').trim().toLowerCase().replace(/-/g, '_')
    return GITALK_LANGUAGE[key] || 'en'
  }

  function isConfigured(config) {
    if (!config || config.enabled === false) return false
    if (!config.clientID || !config.repo || !config.owner) return false
    const admin = (config.admin || []).filter(Boolean)
    return admin.length > 0
  }

  async function loadConfig() {
    for (const url of CONFIG_URLS) {
      try {
        const res = await fetch(url)
        if (res.ok) return await res.json()
      } catch {
        // try next
      }
    }
    return null
  }

  function syncHashGitalkRaw(raw) {
    const parts = []
    for (let seed = 0; seed < 4; seed++) {
      let hash = seed
      for (let i = 0; i < raw.length; i++) {
        hash = Math.imul(hash ^ raw.charCodeAt(i), 0x5bd1e995)
        hash = (hash ^ (hash >>> 15)) >>> 0
      }
      parts.push(hash.toString(16).padStart(8, '0'))
    }
    return parts.join('')
  }

  async function hashGitalkRaw(raw) {
    if (typeof crypto !== 'undefined' && crypto.subtle) {
      const buf = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(raw))
      return Array.from(new Uint8Array(buf))
        .map((byte) => byte.toString(16).padStart(2, '0'))
        .join('')
    }
    return syncHashGitalkRaw(raw)
  }

  async function gitalkHashedId(sitePrefix, raw) {
    const hash = await hashGitalkRaw(raw)
    const hashBudget = GITALK_ID_MAX - sitePrefix.length - 1
    return `${sitePrefix}/${hash.slice(0, hashBudget)}`
  }

  function localeFromPath() {
    const match = window.location.pathname.match(/\/modern\/field-guide\/([a-z]{2}_[a-z]{2})\//)
    return match ? match[1] : 'en_us'
  }

  async function init() {
    const section = document.getElementById('comments')
    if (!section) return

    const config = await loadConfig()
    if (!isConfigured(config)) return

    section.hidden = false

    const rawKey = section.dataset.gitalkKey || ''
    const title = section.dataset.gitalkTitle || document.title
    const pageUrl = section.dataset.gitalkUrl || window.location.href
    const locale = localeFromPath()
    const id = rawKey ? await gitalkHashedId('field-guide', rawKey) : ''

    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/gitalk@1.8.0/dist/gitalk.min.js'
    script.onload = function () {
      const gitalk = new Gitalk({
        clientID: config.clientID,
        clientSecret: config.clientSecret || '',
        repo: config.repo,
        owner: config.owner,
        admin: config.admin,
        id: id,
        title: title,
        body: ['Field Guide discussion', '', `- Key: \`${rawKey}\``, `- Page: ${pageUrl}`].join('\n'),
        labels: ['field-guide'],
        language: gitalkLanguage(locale),
        distractionFreeMode: config.distractionFreeMode || false,
        createIssueManually: config.createIssueManually || false,
        proxy: config.proxy || undefined,
      })
      gitalk.render('gitalk-container')
    }
    document.body.appendChild(script)
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init)
  } else {
    void init()
  }
})()
