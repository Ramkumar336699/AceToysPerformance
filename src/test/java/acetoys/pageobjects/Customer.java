package acetoys.pageobjects;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Choice;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
public class Customer {
    public static Iterator<Map<String, Object>> loginFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                Random rnd = new Random();
                int userId = rnd.nextInt(3-1+1)+1;

                HashMap<String,Object> hMap = new HashMap<String, Object>();
                hMap.put("userId", "user"+ userId);
                hMap.put("password", "pass");
                return hMap;
    }).iterator();
    public static ChainBuilder login =
            feed(loginFeeder)
                    .exec(
                    http("Login User")
                            .post("/login")
                            .formParam("_csrf", "#{csrfToken}")
                            .formParam("username", "#{userId}")
                            .formParam("password", "#{password}")
                            .check(css("#_csrf","content").saveAs("csrfTokenLoggedIn"))
            )
                    .exec(session -> session.set("customerLoggedIn",true));
                   /* .exec(session -> {
                        System.out.println(session.getString("userId"));
                        return session;
                    });*/

    public static ChainBuilder logout =
            randomSwitch().on(
                    Choice.withWeight(10,exec(
                            http("Logout")
                                    .post("/logout")
                                    .formParam("_csrf","#{csrfTokenLoggedIn}")
//                                    .check(css("NavbarHeaderLink").is("Login"))
                    ))
            );

}
