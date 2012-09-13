/*
 CryptoJS v3.0
 code.google.com/p/crypto-js
 (c) 2009-2012 by Jeff Mott. All rights reserved.
 code.google.com/p/crypto-js/wiki/License

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 - Redistributions of source code must retain the above copyright notice, this
 list of conditions, and the following disclaimer.
 - Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions, and the following disclaimer
 in the documentation or other materials provided with the distribution.
 - Neither the name CryptoJS nor the names of its contributors may be used to
 endorse or promote products derived from this
 software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS," AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, ARE
 DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 PROCUREMENT OF SUBSTITUTE
 GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */
var CryptoJS = CryptoJS || function(h, l) {
  var o = {}, f = o.lib = {}, m = f.Base = function() {
    function a() {
    }

    return {
      extend : function(b) {
        a.prototype = this;
        var c = new a;
        b && c.mixIn(b);
        c.$super = this;
        return c
      },
      create : function() {
        var a = this.extend();
        a.init.apply(a, arguments);
        return a
      },
      init : function() {
      },
      mixIn : function(a) {
        for (var c in a)a.hasOwnProperty(c) && (this[c] = a[c]);
        a.hasOwnProperty("toString") && (this.toString = a.toString)
      },
      clone : function() {
        return this.$super.extend(this)
      }
    }
  }(), n = f.WordArray = m.extend({
    init : function(a, b) {
      a = this.words = a || [];
      this.sigBytes = b != l ? b : 4 * a.length
    },
    toString : function(a) {
      return (a || e).stringify(this)
    },
    concat : function(a) {
      var b = this.words, c = a.words, d = this.sigBytes, a = a.sigBytes;
      this.clamp();
      if (d % 4)
        for (var e = 0; e < a; e++)
          b[d + e >>> 2] |= (c[e >>> 2] >>> 24 - 8 * (e % 4) & 255) << 24 - 8 * ((d + e) % 4);
      else
        b.push.apply(b, c);
      this.sigBytes += a;
      return this
    },
    clamp : function() {
      var a = this.words, b = this.sigBytes;
      a[b >>> 2] &= 4294967295 << 32 - 8 * (b % 4);
      a.length = h.ceil(b / 4)
    },
    clone : function() {
      var a = m.clone.call(this);
      a.words = this.words.slice(0);
      return a
    },
    random : function(a) {
      for (var b = [], c = 0; c < a; c += 4)
        b.push(4294967296 * h.random() | 0);
      return n.create(b, a)
    }
  }), p = o.enc = {}, e = p.Hex = {
    stringify : function(a) {
      for (var b = a.words, a = a.sigBytes, c = [], d = 0; d < a; d++) {
        var e = b[d >>> 2] >>> 24 - 8 * (d % 4) & 255;
        c.push((e >>> 4).toString(16));
        c.push((e & 15).toString(16))
      }
      return c.join("")
    },
    parse : function(a) {
      for (var b = a.length, c = [], d = 0; d < b; d += 2)
        c[d >>> 3] |= parseInt(a.substr(d, 2), 16) << 24 - 4 * (d % 8);
      return n.create(c, b / 2)
    }
  }, g = p.Latin1 = {
    stringify : function(a) {
      for (var b = a.words, a = a.sigBytes, c = [], d = 0; d < a; d++)
        c.push(String.fromCharCode(b[d >>> 2] >>> 24 - 8 * (d % 4) & 255));
      return c.join("")
    },
    parse : function(a) {
      for (var b = a.length, c = [], d = 0; d < b; d++)
        c[d >>> 2] |= (a.charCodeAt(d) & 255) << 24 - 8 * (d % 4);
      return n.create(c, b)
    }
  }, i = p.Utf8 = {
    stringify : function(a) {
      try {
        return decodeURIComponent(escape(g.stringify(a)))
      } catch(b) {
        throw Error("Malformed UTF-8 data");
      }
    },
    parse : function(a) {
      return g.parse(unescape(encodeURIComponent(a)))
    }
  }, j = f.BufferedBlockAlgorithm = m.extend({
    reset : function() {
      this._data = n.create();
      this._nDataBytes = 0
    },
    _append : function(a) {
      "string" == typeof a && ( a = i.parse(a));
      this._data.concat(a);
      this._nDataBytes += a.sigBytes
    },
    _process : function(a) {
      var b = this._data, c = b.words, d = b.sigBytes, e = this.blockSize, g = d / (4 * e), g = a ? h.ceil(g) : h.max((g | 0) - this._minBufferSize, 0), a = g * e, d = h.min(4 * a, d);
      if (a) {
        for (var f = 0; f < a; f += e)
          this._doProcessBlock(c, f);
        f = c.splice(0, a);
        b.sigBytes -= d
      }
      return n.create(f, d)
    },
    clone : function() {
      var a = m.clone.call(this);
      a._data = this._data.clone();
      return a
    },
    _minBufferSize : 0
  });
  f.Hasher = j.extend({
    init : function() {
      this.reset()
    },
    reset : function() {
      j.reset.call(this);
      this._doReset()
    },
    update : function(a) {
      this._append(a);
      this._process();
      return this
    },
    finalize : function(a) {
      a && this._append(a);
      this._doFinalize();
      return this._hash
    },
    clone : function() {
      var a = j.clone.call(this);
      a._hash = this._hash.clone();
      return a
    },
    blockSize : 16,
    _createHelper : function(a) {
      return function(b, c) {
        return a.create(c).finalize(b)
      }
    },
    _createHmacHelper : function(a) {
      return function(b, c) {
        return k.HMAC.create(a, c).finalize(b)
      }
    }
  });
  var k = o.algo = {};
  return o
}(Math);
(function() {
  var h = CryptoJS, l = h.lib, o = l.WordArray, l = l.Hasher, f = [], m = h.algo.SHA1 = l.extend({
    _doReset : function() {
      this._hash = o.create([1732584193, 4023233417, 2562383102, 271733878, 3285377520])
    },
    _doProcessBlock : function(n, h) {
      for (var e = this._hash.words, g = e[0], i = e[1], j = e[2], k = e[3], a = e[4], b = 0; 80 > b; b++) {
        if (16 > b)
          f[b] = n[h + b] | 0;
        else {
          var c = f[b - 3] ^ f[b - 8] ^ f[b - 14] ^ f[b - 16];
          f[b] = c << 1 | c >>> 31
        }
        c = (g << 5 | g >>> 27) + a + f[b];
        c = 20 > b ? c + ((i & j | ~i & k) + 1518500249) : 40 > b ? c + ((i ^ j ^ k) + 1859775393) : 60 > b ? c + ((i & j | i & k | j & k) - 1894007588) : c + ((i ^ j ^ k) - 899497514);
        a = k;
        k = j;
        j = i << 30 | i >>> 2;
        i = g;
        g = c
      }
      e[0] = e[0] + g | 0;
      e[1] = e[1] + i | 0;
      e[2] = e[2] + j | 0;
      e[3] = e[3] + k | 0;
      e[4] = e[4] + a | 0
    },
    _doFinalize : function() {
      var f = this._data, h = f.words, e = 8 * this._nDataBytes, g = 8 * f.sigBytes;
      h[g >>> 5] |= 128 << 24 - g % 32;
      h[(g + 64 >>> 9 << 4) + 15] = e;
      f.sigBytes = 4 * h.length;
      this._process()
    }
  });
  h.SHA1 = l._createHelper(m);
  h.HmacSHA1 = l._createHmacHelper(m)
})();
