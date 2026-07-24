/*
 * This file is a part of the No Steam UUID mod for Mindustry.
 *
 * MIT License
 *
 * Copyright (c) 2023-2026 ZetaMap
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
import arc.util.Log;
import arc.util.OS;

import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.desktop.DesktopLauncher;
import mindustry.mod.Mod;


public class Main extends Mod {
  public static ClientLauncher original;

  // Init ASAP
  static { replacePlatform(); }

  public static void replacePlatform() {
    // Ignore if it's not the steam version or if already initialized
    if (!Vars.steam || original != null) return;
    if (!(Vars.platform instanceof ClientLauncher)) {
      Log.warn("Cannot to hook '@'. Another mod has likely already replaced it.", "Vars.platform");
      return;
    }

    original = (ClientLauncher)Vars.platform;

    // Suppress values to avoid double desktop initialization
    System.setProperty("nodiscord", "true");
    boolean was64Bit = OS.is64Bit; // >b105 only checking for this field
    OS.is64Bit = false;
    try { Version.isSteam = false; } // Doesn't exists in >b156
    catch (NoSuchFieldError ignored) {}
    boolean wasEnabled = Version.enabled; // >b156 are initializing it in the constructor
    Version.enabled = false;
    String lastModifier = Version.modifier;
    Version.modifier ="";

    Vars.platform = new DesktopLauncher(new String[0]) {
      // Redirect ApplicationCore methods as it uses a simple array for listeners
      public void setup() { original.setup(); }
      public void add(ApplicationListener module) { original.add(module); }
      public void resize(int width, int height) { original.resize(width, height); }
      public void update() { original.update(); }
      public void exit() { original.exit(); }
      public void init() { original.init(); }
      public void resume(){ original.resume(); }
      public void pause() { original.pause(); }
      // Redirect method because it uses an internal field
      public void updateRPC() { original.updateRPC(); }

      public String getUUID() {
        if (!Vars.steam) return super.getUUID();
        Vars.steam = false; // Disable temporary the steam version. This shouldn't disrupt anything.
        try { return super.getUUID(); }
        finally { Vars.steam = true; }
      }
    };

    // Set back values
    System.clearProperty("nodiscord");
    OS.is64Bit = was64Bit;
    try { Version.isSteam = true; }
    catch (NoSuchFieldError ignored) {}
    Version.enabled = wasEnabled;
    Version.modifier = lastModifier;
  }
}