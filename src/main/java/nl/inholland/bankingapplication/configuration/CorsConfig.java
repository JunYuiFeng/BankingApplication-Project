//package nl.inholland.bankingapplication.configuration;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//
//                .allowedOrigins("http://localhost:5173", "http://localhost:5174" , "http://localhost:5175" ,"http://localhost:5176" ,"http://localhost:5177" , "http://localhost:5178")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
//}
