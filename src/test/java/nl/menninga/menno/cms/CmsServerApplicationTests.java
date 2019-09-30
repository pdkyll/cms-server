package nl.menninga.menno.cms;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"eureka.client.enabled:false"})
public class CmsServerApplicationTests {

	@LocalServerPort
    int port;

    @Before
    public void setUp() {
    	RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    
    @Test
    public void contextLoad(){
    	
    }
}
