package com.mkbrv.orange.cloud.service;


import com.google.gson.JsonDeserializer;
import com.mkbrv.orange.httpclient.response.OrangeResponse;
import com.mkbrv.orange.httpclient.security.OrangeAccessToken;
import com.mkbrv.orange.cloud.AbstractOrangeCloudAPITests;
import com.mkbrv.orange.cloud.model.OrangeFolder;
import com.mkbrv.orange.cloud.model.folder.DefaultOrangeFolder;
import com.mkbrv.orange.cloud.model.freespace.OrangeFreeSpace;
import com.mkbrv.orange.cloud.model.freespace.FreeSpaceDeserializer;
import com.mkbrv.orange.cloud.response.GenericResponse;
import org.junit.gen5.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertFalse;
import static org.junit.gen5.api.Assertions.assertNotNull;
import static org.junit.gen5.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by mkbrv on 20/02/16.
 */
public class OrangeCloudFoldersAPITests extends AbstractOrangeCloudAPITests {


    @Test
    @org.junit.Test
    public void getAvailableSpaceTest() {
        final Long expectedSize = 200L;
        when(orangeHttpClient.doGet(any())).thenReturn(new OrangeResponse() {
            {
                setStatus(200);
                setBody("{\n" +
                        "  \"freespace\": " + expectedSize + "\n" +
                        "}");
            }
        });

        OrangeAccessToken orangeAccessToken = new OrangeAccessToken("token");
        OrangeFreeSpace freeSpace = orangeCloudFoldersAPI.getAvailableSpace(orangeAccessToken);
        assertNotNull(freeSpace);
        assertEquals(expectedSize, freeSpace.getAvailableSpace());
    }


    @Test
    @org.junit.Test
    public void getRootFolderTest() {
        when(orangeHttpClient.doGet(any())).thenReturn(new OrangeResponse() {
            {
                setStatus(200);
                setBody(readResponseAsJson("rootfolder.json"));
            }
        });

        OrangeAccessToken orangeAccessToken = new OrangeAccessToken("token");
        OrangeFolder rootFolder = orangeCloudFoldersAPI.getRootFolder(orangeAccessToken, null);
        assertNotNull(rootFolder);
        assertEquals("Lw", rootFolder.getParentFolderId());
        assertTrue(rootFolder.getSubFolders().size() > 0);
        assertTrue(rootFolder.getFiles().size() > 0);
        assertNotNull(rootFolder.getFiles().get(0).getCreationDate());
    }


    @Test
    @org.junit.Test
    public void createFolderTest() {
        when(orangeHttpClient.doPost(any())).thenReturn(new OrangeResponse() {
            {
                setStatus(200);
                setBody(readResponseAsJson("folder.json"));
            }
        });
        OrangeAccessToken orangeAccessToken = new OrangeAccessToken("token");
        OrangeFolder orangeFolder = orangeCloudFoldersAPI.createFolder(orangeAccessToken, null);
        assertNotNull(orangeFolder);
        assertNotNull(orangeFolder.getParentFolderId());
    }


    @Test
    @org.junit.Test
    public void getFolderTest() {
        when(orangeHttpClient.doGet(any())).thenReturn(new OrangeResponse() {
            {
                setStatus(200);
                setBody(readResponseAsJson("folder.json"));
            }
        });

        OrangeAccessToken orangeAccessToken = new OrangeAccessToken("token");
        OrangeFolder orangeFolder = orangeCloudFoldersAPI.getFolder(orangeAccessToken,
                new DefaultOrangeFolder("random"), null);
        assertNotNull(orangeFolder);
        assertNotNull(orangeFolder.getParentFolderId());
        assertTrue(orangeFolder.getSubFolders().size() > 0);
        assertTrue(orangeFolder.getFiles().size() > 0);
        assertNotNull(orangeFolder.getFiles().get(0).getCreationDate());
    }


    @Test
    @org.junit.Test
    public void deleteFolderTest() {
        when(orangeHttpClient.delete(any())).thenReturn(new OrangeResponse() {
            {
                setStatus(204);
                setBody("");
            }
        });

        OrangeAccessToken orangeAccessToken = new OrangeAccessToken("token");
        GenericResponse response = orangeCloudFoldersAPI.deleteFolder(orangeAccessToken,
                new DefaultOrangeFolder("random"));
        assertNotNull(response);
        assertTrue(response.isOperationSuccessful());
    }

    @Test
    @org.junit.Test
    public void deleteFolderFailureTest() {
        when(orangeHttpClient.delete(any())).thenReturn(new OrangeResponse() {
            {
                setStatus(400);
                setBody("{\n" +
                        "  \"code\": 800,\n" +
                        "  \"message\": \"PDK_CW_0002-INVALID_PARAMETER\",\n" +
                        "  \"description\": \"Invalid some parameter\",\n" +
                        "  \"infoURL\": \"http://orange.com\"\n" +
                        "}");
            }
        });

        OrangeAccessToken orangeAccessToken = new OrangeAccessToken("token");
        GenericResponse response = orangeCloudFoldersAPI.deleteFolder(orangeAccessToken,
                new DefaultOrangeFolder("random"));
        assertNotNull(response);
        assertFalse(response.isOperationSuccessful());
    }

    @Test
    @org.junit.Test
    public void setGsonTypeAdaptersTest() throws IOException {
        DefaultOrangeCloudFoldersAPI orangeCloudFoldersAPI =
                new DefaultOrangeCloudFoldersAPI(this.orangeContext, this.orangeHttpClient);

        FreeSpaceDeserializer freeSpaceDeserializer = Mockito.mock(FreeSpaceDeserializer.class);
        OrangeFreeSpace expectedFreeSpace = new OrangeFreeSpace(1234L);

        Map<Type, JsonDeserializer> gsonAdapters =
                Collections.unmodifiableMap(Stream.of(
                        new AbstractMap.SimpleEntry<>(OrangeFreeSpace.class, freeSpaceDeserializer))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

        orangeCloudFoldersAPI.setGsonTypeAdapters(gsonAdapters);
        when(freeSpaceDeserializer.deserialize(any(), any(), any())).thenReturn(expectedFreeSpace);

        assertEquals(expectedFreeSpace, orangeCloudFoldersAPI.gson.getAdapter(OrangeFreeSpace.class).fromJson("{}"));
    }


}
