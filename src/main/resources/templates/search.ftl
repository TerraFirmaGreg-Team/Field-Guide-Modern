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

    <#include "includes/navbar.ftl">

    <div class="container">
      <div class="row">
        <!-- Table of contents -->
        <nav class="col-md-3">
          <p class="mb-3"><strong>${text_contents}</strong></p>

          <ul class="list-unstyled lh-lg">
            <li><a href="${index}">${text_index}</a></li>
            <#list categories as category>
            <li><a href="${category.id}.html">${category.name}</a></li>
            </#list>
          </ul>
        </nav>

        <!-- Page content -->
        <div class="col-md-9">
          <nav class="mb-4" aria-label="breadcrumb">
            <ol class="breadcrumb">
              <li class="breadcrumb-item active" aria-current="page">
                <i class="bi bi-house-fill"></i>
              </li>
            </ol>
          </nav>

          <ul id="results"></ul>
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
    <script src="https://cdn.jsdelivr.net/npm/fuse.js@7.1.0"></script>
    <script src="${root}/static/icon-carousel.js"></script>
    <script src="${root}/static/icons.min.js"></script>
    <script src="${root}/static/tooltips.js"></script>
    <script src="${root}/static/search.js"></script>
  </body>
</html>