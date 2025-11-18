package io.github.tfgcn.fieldguide.asset;

import io.github.tfgcn.fieldguide.Constants;
import io.github.tfgcn.fieldguide.data.patchouli.Book;
import io.github.tfgcn.fieldguide.data.patchouli.BookCategory;
import io.github.tfgcn.fieldguide.data.patchouli.BookEntry;
import io.github.tfgcn.fieldguide.gson.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static io.github.tfgcn.fieldguide.Constants.BOOK_ID;

@Slf4j
public class AssetLoaderTest {

    static AssetLoader loader;

    @BeforeAll
    static void init() {
        Path path = Paths.get("Modpack-Modern");
        loader = new AssetLoader(path);
    }

    @Test
    void test() {
        Assertions.assertTrue(true);
    }

    @Test
    void testLoadBook() throws IOException {
        Book book = loader.loadBook(BOOK_ID);
        for (String lang : Constants.LANGUAGES) {
            Book localizedBook = loader.loadBook(BOOK_ID, lang, book);
            localizedBook.report();
        }
    }

}
