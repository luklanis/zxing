/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.result;

import com.google.zxing.Result;

/**
 * Tries to parse results that are a URI of some kind.
 * 
 * @author Sean Owen
 */
public final class URIResultParser extends ResultParser {

  @Override
  public URIParsedResult parse(Result result) {
    String rawText = result.getText();
    // We specifically handle the odd "URL" scheme here for simplicity
    if (rawText.startsWith("URL:")) {
      rawText = rawText.substring(4);
    }
    rawText = rawText.trim();
    if (!isBasicallyValidURI(rawText)) {
      return null;
    }
    return new URIParsedResult(rawText, null);
  }

  /**
   * Determines whether a string is not obviously not a URI. This implements crude checks; this class does not
   * intend to strictly check URIs as its only function is to represent what is in a barcode, but, it does
   * need to know when a string is obviously not a URI.
   */
  static boolean isBasicallyValidURI(CharSequence uri) {
    if (uri == null) {
      return false;
    }
    int period = -1;
    int colon = -1;
    int length = uri.length();
    for (int i = length - 1; i >= 0; i--) {
      char c = uri.charAt(i);
      if (c <= ' ') { // covers space, newline, and more
        return false;
      } else if (c == '.') {
        period = i;
      } else if (c == ':') {
        colon = i;
      }
    }
    // Look for period in a domain but followed by at least a two-char TLD
    // Forget strings that don't have a valid-looking protocol
    if (period >= uri.length() - 2 || (period <= 0 && colon <= 0)) {
      return false;
    }
    if (colon >= 0) {
      if (period < 0 || period > colon) {
        // colon ends the protocol
        if (!isSubstringOfAlphaNumeric(uri, 0, colon)) {
          return false;
        }
      } else {
        // colon starts the port; crudely look for at least two numbers
        if (colon >= uri.length() - 2) {
          return false;
        }
        if (!isSubstringOfDigits(uri, colon + 1, 2)) {
          return false;
        }
      }
    }
    return true;
  }

}