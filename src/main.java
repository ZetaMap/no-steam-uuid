public class main extends mindustry.mod.Mod {
  static { 
    mindustry.core.Platform original = mindustry.Vars.platform;
    mindustry.Vars.platform = new mindustry.ClientLauncher() {
      @Override
      public arc.struct.Seq<arc.files.Fi> getWorkshopContent(Class<? extends mindustry.type.Publishable> type) { return original.getWorkshopContent(type); }
      @Override
      public void viewListing(mindustry.type.Publishable pub) { original.viewListing(pub); }
      @Override
      public void viewListingID(String id) { original.viewListingID(id); }
      @Override
      public mindustry.net.Net.NetProvider getNet() { return original.getNet(); }
      @Override
      public void openWorkshop() { original.openWorkshop(); }
      @Override
      public void publish(mindustry.type.Publishable pub) { original.publish(pub); }
      @Override
      public void inviteFriends() { original.inviteFriends(); }
      @Override
      public void updateLobby() { original.updateLobby(); }
      @Override
      public void updateRPC() { original.updateRPC(); }
    }; 
  }
}