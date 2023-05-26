
    alert("SK PAPA!");
    function constructor(enabled) {
      // this.filters = [
      //     new CosmeticFilter(enabled),
      //     new Branding(enabled),
      //     new Dialog(enabled),
      // ];
      this.enabled = enabled;
      this.pressNextButtonsWhenMounted();
      this.skipVideosWhenShowing();
      this._handleMessage = this._handleMessage.bind(this);
      // chrome.runtime.onMessage.addListener(this._handleMessage);
  }
  function  _handleMessage({
      action,
      payload
  }) {
      if (action === "CHANGE_SETTINGS") {
          if (payload.enabled) {
              this.enable();
          } else {
              this.disable();
          }
      }
  }
  function enable() {
      this.filters.forEach((filter) => {
          filter.enable();
      });
      this.enabled = true;
  }
  function disable() {
      this.filters.forEach((filter) => {
          filter.disable();
      });
      this.enabled = false;
  }
  /**
   * Some video ads render the skip button first hidden.
   * If we find it, let"s press skip!
   */
   function pressNextButtonsWhenMounted() {
      if (location.host.includes(".youtube.com")) {
          onSkipBtnMounted(".ytp-ad-skip-button.ytp-button", (btn) => {
              if (this.enabled) {
                  console.log("Clicked skip-ad button");
                  btn.click();
              }
          });
      }
  }
  /**
   * Some new video ads don"t render a skip button,
   * so let"s skip through the ad
   */
   function skipVideosWhenShowing() {
      if (location.host.includes(".youtube.com")) {
          onSkipBtnMounted(".html5-video-player.ad-showing video", (video) => {
              if (this.enabled) {
                  console.log("skipped video ads");
                  video.currentTime = 10000;
              }
          });
      }
  }
  function toggle() {
      if (this.enabled) {
          this.disable();
      } else {
          this.enable();
      }
  }


function waitFor(selector, callback, timeout) {
  const element = document.querySelector(selector);
  if (element) {
      callback(element);
  } else {
      if (timeout) {
          return window.setTimeout(() => {
              return window.requestAnimationFrame(() => {
                  waitFor(selector, callback);
              });
          }, timeout);
      }
      return window.requestAnimationFrame(() => {
          waitFor(selector, callback);
      });
  }
}
function headReady(callback) {
  if (document.readyState === "complete") {
      callback();
      return;
  }
  const observer = new MutationObserver(function(mutations) {
      mutations.forEach(function(m) {
          if (
              m.addedNodes &&
              m.addedNodes[0] &&
              m.addedNodes[0].nodeName === "BODY"
          ) {
              callback();
              observer.disconnect();
          }
      });
  });
  observer.observe(document.documentElement, {
      childList: true
  });
}
function domReady(callback) {
  if (document.readyState === "complete") {
      callback();
  } else {
      window.addEventListener("load", callback, {
          once: true,
      });
  }
}
function onSkipBtnMounted(selector, callback) {
  function check(mutation) {
      const $found = document.querySelector(selector);
      if ($found) {
          return callback($found);
      }
  }
  check();
  const player = document.getElementsByTagName("ytd-player")[0];
  if (!player) {
      return setTimeout(() => {
          onSkipBtnMounted(selector, callback);
      }, 300);
  }
  console.log("mount observer");
  if (document.querySelector("ytd-display-ad-renderer")) {
      document.querySelector("ytd-display-ad-renderer")
          .closest("ytd-rich-item-renderer")
          .style.display = "none"
  }
  const observer = new MutationObserver(check);
  observer.observe(player, {
      childList: true,
      subtree: true,
  });
}

constructor(true);
