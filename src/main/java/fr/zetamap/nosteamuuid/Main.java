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
import arc.Core;
import arc.struct.Seq;
import arc.util.*;
import arc.util.serialization.Json;

import mindustry.Vars;
import mindustry.core.Version;
import mindustry.desktop.DesktopLauncher;
import mindustry.mod.Mod;


public class Main extends Mod {
  // Init ASAP and ignore if it's not the steam version
  static { if (Vars.steam) {
    boolean replacePlatform = true;

    // Suppress values to avoid double desktop initialization
    System.setProperty("nodiscord", "true");
    boolean was64Bit = OS.is64Bit; // <b105 are only checking for this field
    OS.is64Bit = false;
    try { Version.isSteam = false; } // Doesn't exists in <b156
    catch (NoSuchFieldError ignored) {}
    boolean wasEnabled = Version.enabled; // <b156 are initializing it in the constructor
    Version.enabled = false;
    String lastModifier = Version.modifier;
    Version.modifier ="";

    // New launcher that overrides uuid getter
    DesktopLauncher newPlatform = new DesktopLauncher(new String[0]) {
      public String getUUID() {
        if (!Vars.steam) return super.getUUID();
        Vars.steam = false; // Disable temporary the steam version. This shouldn't disrupt anything.
        try { return super.getUUID(); }
        finally { Vars.steam = true; }
      }
    };

    // Copy fields and replace listener
    try { new Json().copyFields(Vars.platform, newPlatform, true); }
    catch (Exception e) { Log.warn("Unable to properly hook '@': @.", "Vars.platform", e.toString()); }
    catch (Throwable ignored) { // Older versions didn't have the 'setFinals' argument
      try { new Json().copyFields(Vars.platform, newPlatform); }
      catch (Exception e) { Log.warn("Unable to properly hook '@': @.", "Vars.platform", e.toString()); }
      catch (Throwable th) {
        Log.err("Unable to hook 'Vars.platform'", th);
        replacePlatform = false;
      }
    }
    try {
      Seq<ApplicationListener> listeners = Core.app.getListeners();
      int index = listeners.indexOf((ApplicationListener)Vars.platform);
      if (index != -1) listeners.set(index, newPlatform);
      else listeners.insert(0, newPlatform); // Usually the first listener
    } catch (Throwable ignored) { // In <b105, Seq was Array. Use reflection
      try {
        Object listners = Reflect.invoke(Core.app, "getListeners");
        int index = Reflect.invoke(listners, "indexOf", new Object[] {Vars.platform}, Object.class);
        if (index != -1) Reflect.invoke(listners, "set", new Object[] {index, newPlatform}, int.class, Object.class);
        else Reflect.invoke(listners, "insert", new Object[] {0, newPlatform}, int.class, Object.class);
      } catch (Throwable th) {
        Log.err("Unable to hook 'Vars.platform' via reflection", th);
        replacePlatform = false;
      }
    }

    // Set back values
    System.clearProperty("nodiscord");
    OS.is64Bit = was64Bit;
    try { Version.isSteam = true; }
    catch (NoSuchFieldError ignored) {}
    Version.enabled = wasEnabled;
    Version.modifier = lastModifier;

    // And replace existing platform if everything is fine
    if (replacePlatform) Vars.platform = newPlatform;
  }}
}