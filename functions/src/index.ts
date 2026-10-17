import * as admin from "firebase-admin";

admin.initializeApp();

export {
  onConnectionCreated,
  onConnectionUpdated,
  onOutcomeUpdated,
} from "./notifications";

export {
  aggregateCampusTelemetry,
  generateInstitutionalReport,
} from "./telemetry";
