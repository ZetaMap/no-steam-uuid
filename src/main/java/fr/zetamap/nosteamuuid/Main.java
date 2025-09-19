/*
 * This file is a part of the No Steam UUID plugin for Mindustry.
 *
 * MIT License
 *
 * Copyright (c) 2023-2025 ZetaMap
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.zetamap.nosteamuuid;

import arc.ApplicationListener;
import arc.files.Fi;
import arc.struct.Seq;

import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.net.Net;
import mindustry.type.Publishable;


public class Main extends Mod {
  static { 
    // Ignore if it's not the steam version
    if (Vars.steam) {
      // Assume this is at least a {@link mindustry.ClientLauncher} since only this class set this field.
      final ClientLauncher original = (ClientLauncher)Vars.platform;
      
      Vars.platform = new ClientLauncher() {
        // Methods from {@link mindustry.ClientLauncher}
        public void add(ApplicationListener module) { original.add(module); }
        public void resize(int width, int height) { original.resize(width, height); }
        public void update() { original.update(); }
        public void init() { original.init(); }
        public void resume(){ original.resume(); }
        public void pause() { original.pause(); }
        
        // Methods from {@link mindustry.desktop.DesktopLauncher}
        public Seq<Fi> getWorkshopContent(Class<? extends Publishable> type) { return original.getWorkshopContent(type); }
        public void viewListing(Publishable pub) { original.viewListing(pub); }
        public void viewListingID(String id) { original.viewListingID(id); }
        public Net.NetProvider getNet() { return original.getNet(); }
        public void openWorkshop() { original.openWorkshop(); }
        public void publish(Publishable pub) { original.publish(pub); }
        public void inviteFriends() { original.inviteFriends(); }
        public void updateLobby() { original.updateLobby(); }
        public void updateRPC() { original.updateRPC(); }
        
        // Not overridden to keep method from {@link mindustry.core.Platform#getUUID()}
        //public String getUUID() { return original.getUUID(); }
      };   
    }
  }
}