package at.fhtw.jira.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefectValues {
  Test("Test"),
  Pilot("Pilot"),
  Kundenabnahme("Kundenabnahme"),
  Produktion("Produktion"),
  MO("M0-Alarm"),
  M1("M1-kritisch"),
  M2("M2-signifikant"),
  M3("M3-managebar");

  private final String value;
}
