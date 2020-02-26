package cc.prather.tr0wel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class Main {
	public static ConfigurableApplicationContext springContext;
	
	public static void main(String[] args) {
		springContext = SpringApplication.run(Main.class, args);
		FxLauncher.startFxApplication(args);
	}
}
