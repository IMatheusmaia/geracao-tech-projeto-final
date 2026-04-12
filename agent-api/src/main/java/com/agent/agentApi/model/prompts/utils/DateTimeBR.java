package com.agent.agentApi.model.prompts.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record DateTimeBR(
  String time
) {
  public static String getTime() {
      ZoneId zone = ZoneId.of("America/Sao_Paulo");

    var localTime = ZonedDateTime.now(zone);

    DateTimeFormatter brFormat = DateTimeFormatter.ofPattern(
      "EEEE, dd 'de' MMMM 'de' yyyy 'às' HH:mm:ss", 
      new Locale("pt", "BR")
    );

      return localTime.format(brFormat);
    }
}
