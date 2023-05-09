package se.vgregion.delegation.delegation.view;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:${user.home}/.app/delegation-view/application.properties", ignoreResourceNotFound = false)
public class Config {



}
