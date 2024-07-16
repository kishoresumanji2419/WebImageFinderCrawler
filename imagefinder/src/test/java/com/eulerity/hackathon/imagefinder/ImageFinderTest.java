
package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.google.gson.Gson;

public class ImageFinderTest {

    public HttpServletRequest request;
    public HttpServletResponse response;
    public StringWriter sw;
    public HttpSession session;

    @Before
    public void setUp() throws Exception {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(request.getRequestURI()).thenReturn("/");
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("https://www.amazon.com"));
        session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);
    }


   // negative test condition where amazon is returning lot of imageURLS where I am not able to test with mockedimageurls.
    @Test
    public void test() throws IOException, ServletException {
        Mockito.when(request.getServletPath()).thenReturn("/main");
        Mockito.when(request.getParameter("url")).thenReturn("https://amazon.com");

        // Mocking the WebpageImageCrawler to return a predefined set of image URLs
        Set<String> mockImageUrls = new HashSet<>();
        mockImageUrls.add("https://m.media-amazon.com/images/I/619MWLRh6tL._AC_UL320_.jpg");
        mockImageUrls.add("https://m.media-amazon.com/images/I/41CvXMapZoL._AC_UF480,480_SR480,480_.jpg");

        try (MockedStatic<WebpageImageCrawler> mockedStatic = Mockito.mockStatic(WebpageImageCrawler.class)) {

            Set<String> ActualResultSet=WebpageImageCrawler.extractImageUrls("https://amazon.com");

            new ImageFinder().doPost(request, response);
            Assert.assertEquals(mockImageUrls, ActualResultSet);

            //Assert.assertEquals(new Gson().toJson(mockImageUrls), sw.toString());
        }
    }
}


