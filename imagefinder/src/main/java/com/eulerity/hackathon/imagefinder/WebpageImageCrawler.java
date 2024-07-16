
package com.eulerity.hackathon.imagefinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * A utility class for crawling webpages and extracting image URLs.
 */
public class WebpageImageCrawler {

    // Sets to store the URLs encountered during the session
    private static final Set<String> currentSessionUrls = new HashSet<>();

    //private static final Set<String> finalUrlList = new HashSet<>();


    /**
     * Extracts image URLs from the provided base URL, including nested pages.
     *
     * @param baseUrl the base URL to crawl
     * @return a set of image URLs
     */
    public static Set<String> extractImageUrls(String baseUrl) {
        // Check if the base URL has already been processed
        Set<String> finalUrlList = new HashSet<>();
        if (currentSessionUrls.contains(baseUrl)) {
            System.out.println("Base URL is already used");
            // Considering as a single user application and when the user enters the same URL second time within a single session, then I am returning the below Image as error,
            // The 400 bad request image is just for reference even it is not the correct image, for duplicate URL error.
            String duplicateUrl = "https://media.licdn.com/dms/image/D4D12AQGrAdtEoPYS_Q/article-cover_image-shrink_720_1280/0/1686645476540?e=2147483647&v=beta&t=bR0eZ6x91t9TIkyT5LZ3dI7RMzIXm1l05dVAlZvLTCg";
            finalUrlList.add(duplicateUrl);

            return finalUrlList;
        }

        currentSessionUrls.add(baseUrl);

        // Get image URLs from the base URL
        finalUrlList.addAll(getImageUrls(baseUrl));

        // Fetch nested pages from the base URL
        Set<String> nestedPages = fetchNestedPages(baseUrl);

        // Use a thread pool to process nested pages in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(nestedPages.size());

        // Set to hold Futures for asynchronous tasks that will return a Set of String URLs
        Set<Future<Set<String>>> futures = new HashSet<>();

        for (String nestedPage : nestedPages) {
            Callable<Set<String>> task = () -> getImageUrls(nestedPage);
            Future<Set<String>> future = executorService.submit(task);
            futures.add(future);
        }

        // Collect image URLs from nested pages
        for (Future<Set<String>> future : futures) {
            try {
                finalUrlList.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        return finalUrlList;
    }

    /**
     * Fetches image URLs from a specific URL.
     *
     * @param url the URL to fetch images from
     * @return a set of image URLs
     */
    private static Set<String> getImageUrls(String url) {
        Set<String> imageUrls = new HashSet<>();
        if (url != null) {
            System.out.println("Processing URL: " + url);
            Document document = getDocument(url);
            Elements imgElements = document.select("img[src]");
            for (Element img : imgElements) {
                String imageUrl = img.attr("abs:src");
                if (!imageUrl.isEmpty()) {
                    imageUrls.add(imageUrl);
                }
            }
        }
        return imageUrls;
    }

    /**
     * Fetches a Jsoup Document from a given URL.
     *
     * @param url the URL to fetch the document from
     * @return the Jsoup Document
     */
    private static Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch document from URL: " + url, e);
        }
        return document;
    }

    /**
     * Fetches nested pages from a given URL.
     *
     * @param url the URL to fetch nested pages from
     * @return a set of nested page URLs
     */
    private static Set<String> fetchNestedPages(String url) {
        Set<String> nestedPages = new HashSet<>();
        Document document = getDocument(url);
        Elements nestedElements = document.select("a[href]");

        for (Element href : nestedElements) {
            String nestedPageUrl = href.attr("abs:href");
            if (!nestedPageUrl.isEmpty() && nestedPageUrl.contains(url) && !nestedPageUrl.endsWith(".pdf")) {
                nestedPages.add(nestedPageUrl);
            }
        }
        return nestedPages;
    }
}

