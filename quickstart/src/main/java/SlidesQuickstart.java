// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START slides_quickstart]
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.AffineTransform;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.CreateImageRequest;
import com.google.api.services.slides.v1.model.CreateImageResponse;
import com.google.api.services.slides.v1.model.CreateParagraphBulletsRequest;
import com.google.api.services.slides.v1.model.CreateShapeRequest;
import com.google.api.services.slides.v1.model.CreateShapeResponse;
import com.google.api.services.slides.v1.model.CreateSlideRequest;
import com.google.api.services.slides.v1.model.CreateSlideResponse;
import com.google.api.services.slides.v1.model.Dimension;
import com.google.api.services.slides.v1.model.InsertTextRequest;
import com.google.api.services.slides.v1.model.LayoutReference;
import com.google.api.services.slides.v1.model.Page;
import com.google.api.services.slides.v1.model.PageElementProperties;
import com.google.api.services.slides.v1.model.Presentation;
import com.google.api.services.slides.v1.model.Range;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.Size;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidesQuickstart {
    private static final String APPLICATION_NAME = "Google Slides API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SlidesScopes.PRESENTATIONS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SlidesQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Slides service = new Slides.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
//create a presentation
        Presentation presentation = new Presentation()
                .setTitle("nameofppt");
        presentation = service.presentations().create(presentation)
                .setFields("presentationId")
                .execute();
        System.out.println("Created presentation with ID: " + presentation.getPresentationId());
        
//create new slide
     List<Request> requests = new ArrayList<>();
     String slideId = "idofslide";
     requests.add(new Request()
             .setCreateSlide(new CreateSlideRequest()
                     .setObjectId(slideId)
                     .setInsertionIndex(1)
                     .setSlideLayoutReference(new LayoutReference()
                             .setPredefinedLayout("TITLE_AND_TWO_COLUMNS"))));

     BatchUpdatePresentationRequest body =
             new BatchUpdatePresentationRequest().setRequests(requests);
     BatchUpdatePresentationResponse response1 =
             service.presentations().batchUpdate(presentation.getPresentationId(), body).execute();
     CreateSlideResponse createSlideResponse = response1.getReplies().get(0).getCreateSlide();
     System.out.println("Created slide with ID: " + createSlideResponse.getObjectId());
     
     
     //add images to the slide
     String imageUrl = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";

  // Create a new image, using the supplied object ID, with content downloaded from imageUrl.
  List<Request> requests1 = new ArrayList<>();
  String imageId = "MyImageId_01";
  Dimension emu4M = new Dimension().setMagnitude(4000000.0).setUnit("EMU");
  requests1.add(new Request()
          .setCreateImage(new CreateImageRequest()
                  .setObjectId(imageId)
                  .setUrl(imageUrl)
                  .setElementProperties(new PageElementProperties()
                          .setPageObjectId(slideId)
                          .setSize(new Size()
                                  .setHeight(emu4M)
                                  .setWidth(emu4M))
                          .setTransform(new AffineTransform()
                                  .setScaleX(1.0)
                                  .setScaleY(1.0)
                                  .setTranslateX(100000.0)
                                  .setTranslateY(100000.0)
                                  .setUnit("EMU")))));

  // Execute the request.
  BatchUpdatePresentationRequest body1 =
          new BatchUpdatePresentationRequest().setRequests(requests1);
  BatchUpdatePresentationResponse response =
		  service.presentations().batchUpdate(presentation.getPresentationId(), body1).execute();
  CreateImageResponse createImageResponse = response.getReplies().get(0).getCreateImage();
  System.out.println("Created image with ID: " + createImageResponse.getObjectId());
     
  
//Create a new square text box, using a supplied object ID.
List<Request> requeststextbx = new ArrayList<>();
String textBoxId = "MyTextBox_01";
Dimension pt350 = new Dimension().setMagnitude(350.0).setUnit("PT");
requeststextbx.add(new Request()
       .setCreateShape(new CreateShapeRequest()
               .setObjectId(textBoxId)
               .setShapeType("TEXT_BOX")
               .setElementProperties(new PageElementProperties()
                       .setPageObjectId(slideId)
                       .setSize(new Size()
                               .setHeight(pt350)
                               .setWidth(pt350))
                       .setTransform(new AffineTransform()
                               .setScaleX(1.0)
                               .setScaleY(1.0)
                               .setTranslateX(350.0)
                               .setTranslateY(100.0)
                               .setUnit("PT")))));

//Insert text into the box, using the object ID given to it.
requeststextbx.add(new Request()
       .setInsertText(new InsertTextRequest()
               .setObjectId(textBoxId)
               .setInsertionIndex(0)
               .setText("New Box Text Inserted")));

//Execute the requests.
BatchUpdatePresentationRequest bodytextbx =
       new BatchUpdatePresentationRequest().setRequests(requeststextbx);
BatchUpdatePresentationResponse responsetextbx =
       service.presentations().batchUpdate(presentation.getPresentationId(), bodytextbx).execute();
CreateShapeResponse createShapeResponsetextbx = responsetextbx.getReplies().get(0).getCreateShape();
System.out.println("Created textbox with ID: " + createShapeResponsetextbx.getObjectId());



     // print elements of the created presentation
     Presentation response2 = service.presentations().get(presentation.getPresentationId()).execute();
     
     List<Page> slides = response2.getSlides();

     System.out.printf("The presentation contains %s slides:\n", slides.size());
     for (int i = 0; i < slides.size(); ++i) {
         System.out.printf("- Slide #%s contains %s elements.\n", i + 1, slides.get(i).getPageElements().size());
     }
    }
}
// [END slides_quickstart]
