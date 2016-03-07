package com.mkbrv.orange.integration.cloud;

import com.mkbrv.orange.client.security.OrangeAccessToken;
import com.mkbrv.orange.client.security.OrangeRefreshToken;
import com.mkbrv.orange.cloud.OrangeCloudFoldersAPI;
import com.mkbrv.orange.cloud.impl.OrangeCloudFoldersAPIImpl;
import com.mkbrv.orange.cloud.model.OrangeFileType;
import com.mkbrv.orange.cloud.model.OrangeFolder;
import com.mkbrv.orange.cloud.model.OrangeFreeSpace;
import com.mkbrv.orange.cloud.request.OrangeFolderRequestParams;
import com.mkbrv.orange.integration.identity.ITOrangeIdentityAPI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


/**
 * Created by mkbrv on 20/02/16.
 */
public class ITOrangeCloudFoldersAPI extends ITOrangeIdentityAPI {

    private static final Logger LOG = LoggerFactory.getLogger(ITOrangeCloudFoldersAPI.class);
    OrangeCloudFoldersAPI orangeCloudFoldersAPI;

    @Before
    public void init() throws IOException {
        super.init();
        orangeCloudFoldersAPI = new OrangeCloudFoldersAPIImpl(this.orangeContext);
    }


    @Test
    public void getAvailableFreeSpace() {
        //we were unable to generate this dynamically based on user & pwd. so we can only use temporary ones
        if (this.orangeAccountRefreshToken == null || this.orangeAccountRefreshToken.length() == 0) {
            return;
        }

        OrangeAccessToken orangeAccessToken = this.getOrangeAccessToken();

        assertNotNull(orangeAccessToken);
        OrangeFreeSpace orangeFreeSpace = orangeCloudFoldersAPI.getAvailableSpace(orangeAccessToken);
        LOG.info("Orange Free space found : {} ", orangeFreeSpace);
        assertNotNull(orangeFreeSpace);
        Assert.assertTrue(orangeFreeSpace.getAvailableSpace() > 0L);
    }

    @Test
    public void getRootFolder() {
        //we were unable to generate this dynamically based on user & pwd. so we can only use temporary ones
        if (this.orangeAccountRefreshToken == null || this.orangeAccountRefreshToken.length() == 0) {
            return;
        }
        OrangeAccessToken orangeAccessToken = this.getOrangeAccessToken();

        assertNotNull(orangeAccessToken);
        OrangeFolder orangeFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken, null);

        assertNotNull(orangeFolder);
    }


    @Test
    public void flatRootFolderReturnsAllFiles() {
        //we were unable to generate this dynamically based on user & pwd. so we can only use temporary ones
        if (this.orangeAccountRefreshToken == null || this.orangeAccountRefreshToken.length() == 0) {
            return;
        }
        OrangeAccessToken orangeAccessToken = this.getOrangeAccessToken();

        OrangeFolder regularRootFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken,
                new OrangeFolderRequestParams());

        OrangeFolder flatRootFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken,
                new OrangeFolderRequestParams()
                        .setFlat(Boolean.TRUE));
        assertNotNull(flatRootFolder);


        //we expect to get all pictures and folders in one call.
        assertTrue(flatRootFolder.getFiles().size() > regularRootFolder.getFiles().size());
        assertTrue(flatRootFolder.getSubFolders().size() > regularRootFolder.getSubFolders().size());
    }

    @Test
    public void restrictedModeReturnsAppFolderAsRoot() {
        //we were unable to generate this dynamically based on user & pwd. so we can only use temporary ones
        if (this.orangeAccountRefreshToken == null || this.orangeAccountRefreshToken.length() == 0) {
            return;
        }
        OrangeAccessToken orangeAccessToken = this.getOrangeAccessToken();

        OrangeFolder regularRootFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken,
                new OrangeFolderRequestParams());

        OrangeFolder restrictedFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken,
                new OrangeFolderRequestParams()
                        .setRestrictedMode(""));

        assertNotNull(restrictedFolder);
        assertNotEquals(restrictedFolder.getId(), regularRootFolder.getId());
    }

    @Test
    public void showThumbnailsReturnsFileThumbnail() {
        //we were unable to generate this dynamically based on user & pwd. so we can only use temporary ones
        if (this.orangeAccountRefreshToken == null || this.orangeAccountRefreshToken.length() == 0) {
            return;
        }
        OrangeAccessToken orangeAccessToken = this.getOrangeAccessToken();

        OrangeFolder rootFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken,
                new OrangeFolderRequestParams().setShowThumbnails(""));

        //do they all have it? Expect at least one to have them might be better
        final AtomicInteger countHasPreviewUrl = new AtomicInteger(0);
        rootFolder.getFiles().forEach((file) -> {
            if (file.getPreviewUrl() != null && file.getThumbUrl() != null) {
                countHasPreviewUrl.incrementAndGet();
            }
        });

        if (rootFolder.getFiles().size() > 0) {
            assertTrue(countHasPreviewUrl.get() > 0);
        }
    }

    /**
     * Account must have audio files. Flat is set to true
     */
    @Test
    public void filterReturnsOnlyAudioFiles() {
        //we were unable to generate this dynamically based on user & pwd. so we can only use temporary ones
        if (this.orangeAccountRefreshToken == null || this.orangeAccountRefreshToken.length() == 0) {
            return;
        }

        OrangeAccessToken orangeAccessToken = this.getOrangeAccessToken();

        OrangeFolder rootFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken,
                new OrangeFolderRequestParams().setFlat(Boolean.TRUE).setFilter(OrangeFileType.AUDIO));

        rootFolder.getFiles().forEach((file) -> {
            assertTrue(file.getType().equals(OrangeFileType.AUDIO));
        });

        rootFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken,
                new OrangeFolderRequestParams().setFlat(Boolean.TRUE).setFilter(OrangeFileType.VIDEO));

        rootFolder.getFiles().forEach((file) -> {
            assertTrue(file.getType().equals(OrangeFileType.VIDEO));
        });
    }


}
