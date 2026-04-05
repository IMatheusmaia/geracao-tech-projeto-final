// Types matching SeacherController.java API responses exactly

// === Search ===
export interface OutputSchema {
  messages: Message[] | null;
  retriveled_data: RetriveledData | null; // matches backend @JsonProperty("retriveled_data")
  searched_data: SearchedData | null;
}

export interface Message {
  content: string;
}

export interface RetriveledData {
  id: string;
  title: string;
  content: string;
  saved_at: string;
  updateded_at: string; // matches backend typo (double 'e')
}

export interface SearchedData {
  title: string;
  content: string;
}

// === Content ===
export interface ContentResponse {
  id: string;
  title: string;
  content: string;
  savedAt: string; // camelCase in ContentResponse record
  updatedAt: string;
}

// === Source ===
export interface SourceRequest {
  name: string;
  baseUrl: string;
}

export interface SourceResponse {
  id: string;
  name: string;
  baseUrl: string;
}

// === UI-specific ===
export type Profile =
  | "economia"
  | "esporte"
  | "saude"
  | "lazer"
  | "ciencia"
  | "financas";

export const PROFILE_LABELS: Record<Profile, string> = {
  economia: "Economia",
  esporte: "Esporte",
  saude: "Saude",
  lazer: "Lazer",
  ciencia: "Ciencia",
  financas: "Financas",
};

export interface ChatMessage {
  id: string;
  role: "user" | "assistant";
  content: string;
  timestamp: Date;
  retriveledData?: RetriveledData;
  searchedData?: SearchedData;
}

export interface ModalData {
  type: "retrieved" | "searched" | "content";
  title: string;
  content: string;
}
