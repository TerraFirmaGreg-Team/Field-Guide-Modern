<!DOCTYPE html>
<html lang="${current_lang.key}">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <#include "includes/seo-head.ftl">

    <title>${long_title}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${handbookIconsRoot}/icons.css">
    <link rel="stylesheet" href="${root}/static/style.css">
  </head>
  <body>
    <!-- Load theme switcher JavaScript early to avoid flashes of incorrectly-themed content -->
    <script src="${root}/static/theme-switcher.js"></script>

    <#assign lang_page = "${current_category.id}.html">
    <#include "includes/navbar.ftl">

    <div class="container">
      <div class="row">
        <!-- Table of contents -->
        <nav class="col-md-3">
          <p class="mb-3"><strong>${text_contents}</strong></p>

          <ul class="list-unstyled lh-lg">
            <li><a href="${index}">${text_index}</a></li>
            <#list categories as category>
            <li>
              <a href="${category.id}.html">${category.name}</a>
              <#if current_category.id == category.id && category.entries?? && category.entries?size gt 0>
              <ul>
                <#list category.entries as entry>
                <li><a href="${entry.id}.html">${entry.name}</a></li>
              </#list>
              </ul>
              </#if>
            </li>
            </#list>
          </ul>
        </nav>

        <!-- Page content -->
        <div class="col-md-9">
          <nav class="mb-4" aria-label="breadcrumb">
            <ol class="breadcrumb">
              <li class="breadcrumb-item">
                <a href="../"><i class="bi bi-house-fill"></i></a>
              </li>
              <li class="breadcrumb-item active" aria-current="page">
                ${current_category.name}
              </li>
            </ol>
          </nav>

          <h1 class="mb-4">${current_category.name}</h1>
          <p>${current_category.description}</p>
          <div class="row row-cols-1 row-cols-md-3 g-3">
            <#list current_category.entries! as entry>
            <div class="col">
              <div class="card">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-2">
                    <#if entry.iconCardHtml?has_content>${entry.iconCardHtml}<#else><img class="entry-card-icon me-2" src="${root}/_images/placeholder_16.png" alt="${entry.name}" /></#if>
                    <a href="${entry.id}.html">${entry.name}</a>
                  </div>
                </div>
              </div>
            </div>
            </#list>
          </div>

          <section id="comments" class="mt-5"
            data-gitalk-key="field-guide/${current_lang.key}/${current_category.id}"
            data-gitalk-title="${current_category.name?html}"
            data-gitalk-url="${canonicalUrl?html}"
            hidden>
            <h2 class="h5 mb-3">${text_comments}</h2>
            <div id="gitalk-container"></div>
          </section>
        </div>
      </div>
    </div>

    <div class="container">
      <footer class="py-3 my-5 border-top">
        <ul class="nav justify-content-center">

          <!-- GitHub repo -->
          <li class="nav-item">
            <a class="nav-link px-3 text-body-secondary" href="https://github.com/TerraFirmaGreg-Team/Field-Guide-Modern">
              <i class="bi bi-github"></i> ${text_github}
            </a>
          </li>

          <!-- Discord server -->
          <li class="nav-item">
            <a class="nav-link px-3 text-body-secondary" href="https://discord.gg/AEaCzCTUwQ">
              <i class="bi bi-discord"></i> ${text_discord}
            </a>
          </li>
        </ul>
      </footer>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script src="${root}/static/icon-carousel.js"></script>
    <script src="${root}/static/icons.min.js"></script>
    <script src="${root}/static/tooltips.js"></script>
    <script src="${root}/static/search.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/gitalk@1.8.0/dist/gitalk.css">
    <link rel="stylesheet" href="${root}/static/gitalk.css">
    <script src="${root}/static/gitalk-init.js"></script>
  </body>
</html>