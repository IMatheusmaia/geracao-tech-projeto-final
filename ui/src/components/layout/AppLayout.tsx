import { useState } from "react";
import { Search, Database, Menu, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { Sheet, SheetContent } from "@/components/ui/sheet";
import { ConversationList } from "@/components/sidebar/ConversationList";
import { ChatInput } from "@/components/chat/ChatInput";
import { MessageList } from "@/components/chat/MessageList";
import { ProfileToggle } from "@/components/chat/ProfileToggle";
import { ContentModal } from "@/components/modals/ContentModal";
import { SourcesModal } from "@/components/modals/SourcesModal";
import { useChatStore } from "@/stores/useChatStore";

function SidebarContent() {
  const { setSourcesModalOpen } = useChatStore();

  return (
    <div className="flex flex-col h-full bg-background">
      <div className="px-4 py-3 flex items-center gap-2">
        <Search className="h-5 w-5 text-primary" />
        <span className="font-semibold text-foreground">Buscador</span>
      </div>
      <Separator />
      <div className="flex-1 overflow-hidden">
        <ConversationList />
      </div>
      <Separator />
      <div className="p-3">
        <Button
          variant="outline"
          size="sm"
          className="w-full gap-2"
          onClick={() => setSourcesModalOpen(true)}
        >
          <Database className="h-4 w-4" />
          Fontes de Pesquisa
        </Button>
      </div>
    </div>
  );
}

export function AppLayout() {
  const { messages, isLoading, clearMessages } = useChatStore();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-background">
      {/* Desktop sidebar */}
      <aside className="hidden md:flex w-[280px] border-r border-border flex-col">
        <SidebarContent />
      </aside>

      {/* Mobile sidebar */}
      <Sheet open={sidebarOpen} onOpenChange={setSidebarOpen}>
        <SheetContent side="left" className="w-[280px] p-0">
          <SidebarContent />
        </SheetContent>
      </Sheet>

      {/* Main chat area */}
      <main className="flex-1 flex flex-col min-w-0">
        {/* Header */}
        <header className="flex items-center gap-2 px-4 py-2 border-b border-border">
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            onClick={() => setSidebarOpen(true)}
          >
            <Menu className="h-5 w-5" />
          </Button>
          <div className="flex-1" />
          <Button
            variant="ghost"
            size="sm"
            className="text-muted-foreground"
            onClick={clearMessages}
          >
            <X className="h-4 w-4 mr-1" />
            Limpar
          </Button>
        </header>

        {/* Profile toggles */}
        <div className="border-b border-border py-2 px-4">
          <ProfileToggle />
        </div>

        {/* Messages */}
        <MessageList messages={messages} isLoading={isLoading} />

        {/* Input */}
        <ChatInput />
      </main>

      {/* Modals */}
      <ContentModal />
      <SourcesModal />
    </div>
  );
}
