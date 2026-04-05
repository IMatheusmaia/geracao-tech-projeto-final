import { create } from "zustand";
import type { ChatMessage, ModalData, Profile } from "@/types/api";

interface ChatState {
  messages: ChatMessage[];
  isLoading: boolean;
  activeProfile: Profile | null;
  modalData: ModalData | null;
  selectedContentId: string | null;
  isSourcesModalOpen: boolean;

  addMessage: (message: ChatMessage) => void;
  clearMessages: () => void;
  setLoading: (loading: boolean) => void;
  setActiveProfile: (profile: Profile | null) => void;
  openModal: (data: ModalData) => void;
  closeModal: () => void;
  setSelectedContentId: (id: string | null) => void;
  setSourcesModalOpen: (open: boolean) => void;
}

export const useChatStore = create<ChatState>((set) => ({
  messages: [],
  isLoading: false,
  activeProfile: null,
  modalData: null,
  selectedContentId: null,
  isSourcesModalOpen: false,

  addMessage: (message) =>
    set((state) => ({ messages: [...state.messages, message] })),

  clearMessages: () => set({ messages: [] }),

  setLoading: (loading) => set({ isLoading: loading }),

  setActiveProfile: (profile) => set({ activeProfile: profile }),

  openModal: (data) => set({ modalData: data }),

  closeModal: () => set({ modalData: null, selectedContentId: null }),

  setSelectedContentId: (id) => set({ selectedContentId: id }),

  setSourcesModalOpen: (open) => set({ isSourcesModalOpen: open }),
}));
