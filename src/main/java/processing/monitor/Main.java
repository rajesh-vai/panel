package processing.monitor;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    static DirectoryWatchService watchService = null;
    public static void main(String[] args) throws Exception{
        try {
        	watchService = new SimpleDirectoryWatchService(); // May throw
            watchService.register( // May throw
                    new DirectoryWatchService.OnFileChangeListener() {
                        @Override
                        public void onFileCreate(String filePath) {
                            System.out.println("Created: "+filePath);
                        }
                
                        @Override
                        public void onFileModify(String filePath) {
                            System.out.println("Modified: "+filePath);
                        }
                        
                        @Override
                        public void onFileDelete(String filePath) {
                            System.out.println("Deleted: "+filePath);
                        }
                    },"D:\\BUILD_FILES\\"
            );
            
            watchService.start();
        } catch (IOException e) {
            LOGGER.error("Unable to register file change listener for ");
        }

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                watchService.stop();
                LOGGER.error("Main thread interrupted.");
                break;
            }
        }
    }
}