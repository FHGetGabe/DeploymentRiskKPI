package at.fhtw.jira.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Application {
  RCI("RCI (AM-585)"),
  TECHNISCHE_INFRASTRUKTUR_BANKARBEITSPLATZ("Technische Infrastruktur Bankarbeitsplatz (AM-132203)"),
  DMS_ARCHIV("DMS/Archiv (AM-287)"),
  DMS_ARCHIV_ABLAGESTANDARD("DMS/Archiv\\Ablagestandard (AM-338)"),
  DMS_ARCHIV_BELEGARCHIVIERUNG("DMS/Archiv\\Belegarchivierung (AM-196)"),
  DMS_ARCHIV_BELEGDOKUMENTE("DMS/Archiv\\Belegdokumente (AM-64147)"),
  DMS_ARCHIV_ELEKTR_KUNDENAKT("DMS/Archiv\\Elektr. Kundenakt (AM-337)"),
  DMS_ARCHIV_KDM("DMS/Archiv\\KDM (AM-436)"),
  DMS_ARCHIV_RESTBELEG_SIENA("DMS/Archiv\\Restbeleg (SIENA) (AM-592)"),
  DMS_ARCHIV_RESTBELEGARCHIV("DMS/Archiv\\Restbelegarchiv (AM-593)"),
  MITARBEITER_MODUL("Mitarbeiter-Modul (AM-504)"),
  SMART_DRB_JOURNAL("SMART/DRB Journal (AM-698)"),
  TECHNISCHE_INFRASTRUKTUR_PAAS_DISCOVERY_ROUTING_PAAS("Technische Infrastruktur PaaS\\Discovery/Routing PaaS (AM-296)"),
  MEINE_BANK("Meine Bank (AM-57407)"),
  SMART_DESKTOP("SMART Desktop (AM-659)"),
  INVARIS_OLD("INVARIS (AM-429)"),
  INVARIS_NEW("INVARIS (AM-57848)"),
  FORMULAR_OUTPUT_MANAGEMENT_INVARIS("Formular Output Management (INVARIS) (AM-382)"),
  AUFGABENLISTE("Aufgabenliste (AM-51)"),
  SMART_SCHALTER("SMART Schalter (AM-55483)"),
  SMART_SCHALTER_KERNBANK_SCHALTER_BUCHUNG("SMART Schalter\\Kernbank Schalter Buchung (AM-277659)"),
  SMART_SCHALTER_KERNBANK_SCHALTER_BUCHUNG_STAMMDATEN("SMART Schalter\\Kernbank Schalter Buchung-Stammdaten (AM-277661)"),
  SMART_SCHALTER_KERNBANK_SCHALTER_KONTO_STAMMDATEN("SMART Schalter\\Kernbank Schalter Konto-Stammdaten (AM-277660)"),
  SMART_GIRO("SMART Giro (AM-683)"),
  SMART_GIRO_AUSKÜNFTE_WARTUNG("SMART Giro Auskünfte/Wartung (AM-665)"),
  SMART_WERTPAPIER("SMART Wertpapier (AM-694)"),
  SMART_WERTPAPIER_RWS_AUFTRAGSPRÜFUNG("SMART Wertpapier\\RWS Auftragsprüfung (AM-695)"),
  SMART_PORTFOLIO("SMART Portfolio (AM-677)"),
  SMART_ANLEGERPROFIL_OLD("SMART Portfolio\\SMART Anlegerprofil (AM-1742957)"),
  SMART_ANLEGERPROFIL_NEW("SMART Portfolio\\SMART Anlegerprofil (AM-678)"),
  SMART_PORTFOLIO_PIA("SMART Portfolio\\PIA - Portfolio Illustrator Advanced (AM-346046)"),
  WERTPAPIER_DEPOT_COCKPIT("Wertpapier-Depot-Cockpit (AM-124530)"),
  E2E_SPEEDKREDIT("E2E SpeedKredit (AM-57867)"),
  FINE("FINE (AM-366)"),
  FINE_ANTRAGSSTATUS_KREDITAKTE("FINE\\FINE Antragsstatus / Kreditakte (AM-368)"),
  RACON_FINANZIERUNG("RACON Finanzierung (AM-543)"),
  RACON_FINANZIERUNG_REPORTING("RACON Finanzierung\\RACON Finanzierung Reporting (AM-545)"),
  RACON_FINANZIERUNG_RFIN_DRUCK_SPA("RACON Finanzierung\\RFIN Druck SPA (AM-352229)"),
  SMART_RATING("SMART Rating (AM-1349022)"),
  SMART_RATING_ANTRAGSSCoring_SELBSTÄNDIG("SMART Rating\\Antragsscoring-Selbständig (AM-631)"),
  SMART_RATING_ANTRAGSSCoring_UNSELBSTÄNDIG("SMART Rating\\Antragsscoring-Unselbständig (AM-632)"),
  SMART_RATING_ANTRAGSSCoring_UNTERNEHMEN("SMART Rating\\Antragsscoring-Unternehmen (AM-577)"),
  SMART_RATING_RETAIL_SCORING_RLB_OÖ("SMART Rating\\Retail Scoring RLB OÖ (AM-124394)"),
  SMART_RATING_CLIENT("SMART Rating\\SMART Rating Client (AM-576)"),
  SMART_RATING_VERHALTENSSCORING_KMU("SMART Rating\\Verhaltensscoring-KMU (AM-635)"),
  SMART_RATING_VERHALTENSSCORING_LANDWIRTE("SMART Rating\\Verhaltensscoring-Landwirte (AM-62999)"),
  SMART_RATING_VERHALTENSSCORING_UNSELBSTÄNDIG("SMART Rating\\Verhaltensscoring-Unselbständig (AM-637)"),
  SMART_RATING_VERHALTENSSCORING_UNTERNEHMEN("SMART Rating\\Verhaltensscoring-Unternehmen (AM-126892)"),
  ZSVZ("ZSVZ (AM-850)"),
  ZSVZ_PROXY("ZSVZ Proxy (AM-1356448)");

  private final String value;

}
