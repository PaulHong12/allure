package com.msa.Youtube.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.msa.Youtube.dto.YoutubeDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class YoutubeService {


    private YoutubeDto youtubeDto;
    private static final String CLIENT_SECRETS = "client_secret.json"; //이거 수정하면 될듯
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");

    private static final String APPLICATION_NAME = "youtube";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
//    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
//        // Load client secrets.
//        InputStream in = YoutubeService.class.getResourceAsStream(CLIENT_SECRETS);
//        GoogleClientSecrets clientSecrets;
//        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow =
//                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
//                        .build();
//        Credential credential =
//                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//        return credential;
//    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                }})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public YoutubeDto YoutubeSearch(String keyword)
            throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();

        // Define and execute the API request
        YouTube.Search.List request = youtubeService.search()
                .list("snippet");
        SearchListResponse response = request
                .setKey("AIzaSyCPgPbUefrGC-lmMV-3SoU_6z2QMcT4SmY")
                .setMaxResults(1L)
                .setQ(keyword)
                .setTopicId("music")
                .setType("video")
                .execute();

        List<SearchResult> searchResultList = response.getItems();

        YoutubeDto youtubeDto1 = null;

        if (searchResultList != null) {
            for (SearchResult rid : searchResultList) {
                youtubeDto1 = new YoutubeDto(
                        rid.getSnippet().getTitle(),
                        rid.getId().getVideoId()
                );
            }
        }

        return youtubeDto1;
    }
}