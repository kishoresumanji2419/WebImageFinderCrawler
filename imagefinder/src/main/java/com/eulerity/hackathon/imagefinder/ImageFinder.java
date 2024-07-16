
package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class ImageFinder
 * This servlet handles POST requests to extract image URLs from a given webpage URL.
 */
@WebServlet(
		name = "ImageFinder",
		urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Gson instance for converting Java objects to JSON format
	private static final Gson GSON = new GsonBuilder().create();

	/**
	 * Handles the HTTP POST request.
	 * Extracts image URLs from the provided webpage URL and returns them as a JSON array.
	 *
	 * @param request  the HttpServletRequest object
	 * @param response the HttpServletResponse object
	 * @throws ServletException if an input or output error occurs
	 * @throws IOException      if the request for the POST could not be handled
	 */
	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");

		// Retrieve the URL parameter from the request
		String webpageUrl = request.getParameter("url");

		// Call the WebpageImageCrawler to extract image URLs from the webpage
		Set<String> imageUrls = WebpageImageCrawler.extractImageUrls(webpageUrl);

		// Convert the set of image URLs to a JSON array and write it to the response
		response.getWriter().print(GSON.toJson(imageUrls));
	}
}



