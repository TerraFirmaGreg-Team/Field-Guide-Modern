package io.github.tfgcn.fieldguide;

import io.github.tfgcn.fieldguide.asset.Asset;
import io.github.tfgcn.fieldguide.asset.AssetLoader;
import io.github.tfgcn.fieldguide.data.patchouli.Book;
import io.github.tfgcn.fieldguide.data.patchouli.BookCategory;
import io.github.tfgcn.fieldguide.data.patchouli.BookEntry;
import io.github.tfgcn.fieldguide.gson.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class Main implements Callable<Integer>  {

    @CommandLine.Option(
            names = {"-i", "--tfg-dir"},
            required = true,
            description = {"The dir of TerraFirmaGreg modpack.",
                    "Support environment TFG_DIR",
                    "e.g. \"/Users/yanmaoyuan/games/tfg-0.11.7\""},
            defaultValue = "${env:TFG_DIR}",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    protected String inputDir;

    @CommandLine.Option(
            names = {"-o", "--out-dir"},
            description = "The dir of output. e.g. \"./output\"",
            defaultValue = "./output",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    protected String outputDir;

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Main());
        System.exit(cmd.execute(args));
    }

    @Override
    public Integer call() throws Exception {
        log.info("Start parsing book..., tfg: {}, out: {}", inputDir, outputDir);

        // The TerraFirmaGreg modpack directory
        String modpackPath = inputDir.replace("\\", "/");

        AssetLoader assetLoader = new AssetLoader(Paths.get(modpackPath));

        Context context = new Context(assetLoader, outputDir, "/Field-Guide-TFG", false);

        BookParser bookParser = new BookParser();
        bookParser.processAllLanguages(context);
        return 0;
    }


    private Book loadBook(AssetLoader loader) throws IOException {
        String bookPath = Constants.getBookPath();
        Asset bookAsset = loader.getAsset(bookPath);

        Book book = JsonUtils.readFile(bookAsset.getInputStream(), Book.class);
        book.setLanguage(Constants.EN_US);
        book.setAssetSource(bookAsset);

        // load categories
        String categoryDir = Constants.getCategoryDir();
        List<Asset> assets = loader.listAssets(categoryDir);
        for (Asset asset : assets) {
            BookCategory category = JsonUtils.readFile(asset.getInputStream(), BookCategory.class);
            category.setAssetSource(categoryDir, asset);

            book.addCategory(category);
        }

        // load entries
        String entryDir = Constants.getEntryDir();
        assets = loader.listAssets(entryDir);
        for (Asset asset : assets) {
            BookEntry entry = JsonUtils.readFile(asset.getInputStream(), BookEntry.class);
            entry.setAssetSource(entryDir, asset);

            book.addEntry(entry);
        }

        book.sort();
        return book;
    }

    private Book loadBook(AssetLoader loader, Book defaultBook, String lang) throws IOException {
        String bookPath = Constants.getBookPath();
        Asset bookAsset = loader.getAsset(bookPath);

        Book book = JsonUtils.readFile(bookAsset.getInputStream(), Book.class);
        book.setLanguage(lang);
        book.setAssetSource(bookAsset);

        String categoryDir = Constants.getCategoryDir(lang);
        String fallbackCategoryDir = Constants.getCategoryDir();
        for (BookCategory category : defaultBook.getCategories()) {
            String path = Constants.getCategoryPath(lang, category.getId());
            Asset asset = loader.getAsset(path);
            if (asset != null) {
                BookCategory localizedCategory = JsonUtils.readFile(asset.getInputStream(), BookCategory.class);
                localizedCategory.setAssetSource(categoryDir, asset);
                book.addCategory(localizedCategory);
            } else {
                // fallback
                path = Constants.getCategoryPath(category.getId());
                asset = loader.getAsset(path);
                BookCategory localizedCategory = JsonUtils.readFile(asset.getInputStream(), BookCategory.class);
                localizedCategory.setAssetSource(fallbackCategoryDir, asset);
                book.addCategory(localizedCategory);
            }
        }

        String entryDir = Constants.getEntryDir(lang);
        String fallbackEntryDir = Constants.getEntryDir();
        for (BookEntry entry : defaultBook.getEntries()) {
            String path = Constants.getEntryPath(lang, entry.getId());
            Asset asset = loader.getAsset(path);
            if (asset != null) {
                BookEntry localizedEntry = JsonUtils.readFile(asset.getInputStream(), BookEntry.class);
                localizedEntry.setAssetSource(entryDir, asset);
                book.addEntry(localizedEntry);
            } else {
                // fallback
                path = Constants.getEntryPath(entry.getId());
                asset = loader.getAsset(path);
                BookEntry localizedEntry = JsonUtils.readFile(asset.getInputStream(), BookEntry.class);
                localizedEntry.setAssetSource(fallbackEntryDir, asset);
                book.addEntry(localizedEntry);
            }
        }

        book.sort();
        return book;
    }
}