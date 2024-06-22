
package MyServletPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.text.DateFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    LocalTime localTime = LocalTime.now();
   DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
   String formattedTime = localTime.format(dateFormatter);
   
   
   //API for Time
  // https://api.ipgeolocation.io/ipgeo?apiKey=abc794c9ce1a42de8fb40b3994769772
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		 //API for Time
		  // https://api.ipgeolocation.io/ipgeo?apiKey=abc794c9ce1a42de8fb40b3994769772
		String apiKey = "abc794c9ce1a42de8fb40b3994769772";
		String localTimeUrl = "https://api.ipgeolocation.io/ipgeo?apiKey=" + apiKey;
	
		  try {
		        URL url = new URL(localTimeUrl);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setRequestMethod("GET");

		        StringBuilder responseContent = new StringBuilder();
		        try (Scanner scanner = new Scanner(connection.getInputStream())) {
		            while (scanner.hasNext()) {
		                responseContent.append(scanner.nextLine());
		            }
		        }

		        Gson gson = new Gson();
		        JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
		        String time = jsonObject.getAsJsonObject("time_zone").get("current_time").getAsString();

		        // Setting the time as request attribute
		        request.setAttribute("currentTime", time);

		        connection.disconnect();
		        
		        // Forwarding to JSP
		        request.getRequestDispatcher("index.jsp").forward(request, response);
		    } catch (IOException e) {
		        // Handle exceptions
		        e.printStackTrace();  // Log the exception for debugging purposes
		        // Optionally, forward to an error page or handle the exception gracefully
		        response.getWriter().append("Error fetching current time: " + e.getMessage());
		    }
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		// TODO Auto-generated method stub
		String inputData = request.getParameter("userInput");
		System.out.println(inputData);
		//API steup
		String apiKey = "bc9b931ab1f44589ff5281a8fba75f8f";
		String city = request.getParameter("city");
		//url edit garera rakheko
		
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
	
		//API Integeration
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		//Reading from network
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		//Storing data
		StringBuilder responseContent = new StringBuilder();
		
		Scanner scanner=new Scanner(reader);
		while(scanner.hasNext()) {
			responseContent.append(scanner.nextLine());
			
		}
		scanner.close();
	
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
	
		long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
		String date = new Date(dateTimestamp).toString();


		//Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
        
     // Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("date", date);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        request.setAttribute("formattedTime", formattedTime);
        connection.disconnect();
        
        request.getRequestDispatcher("index.jsp").forward(request, response);
        
		
	}

}
