package processing.monitor;
import java.io.IOException;

public interface DirectoryWatchService extends Service {

    @Override
    void start(); /* Suppress Exception */

    void register(OnFileChangeListener listener, String dirPath, String... globPatterns)
            throws IOException;

    interface OnFileChangeListener {

         default void onFileCreate(String filePath) {}

        default void onFileModify(String filePath) {}
        default void onFileDelete(String filePath) {}
    }
}