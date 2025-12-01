<template>
  <div class="ocr-page" ref="ocrPageRef">
    <div class="ocr-content">
      <section class="panel upload-panel">
        <header class="ocr-header">
          <div class="header-left">
            <div class="header-tabs">
              <button class="tab is-active">
                <span class="tab-icon" aria-hidden="true">
                  <svg
                      class="icon"
                      viewBox="0 0 1024 1024"
                      xmlns="http://www.w3.org/2000/svg"
                      role="img"
                  >
                    <path
                        d="M312.483343 921.751314H219.044696a118.968331 118.968331 0 0 1-118.968331-118.713034v-93.693944a51.05937 51.05937 0 0 0-100.076365 0v93.693944A219.299993 219.299993 0 0 0 219.044696 1022.082976h93.438647a51.05937 51.05937 0 1 0 0-100.331662zM801.887402 0.895581h-93.949241a51.05937 51.05937 0 1 0 0 100.331662h93.949241a118.968331 118.968331 0 0 1 118.968331 119.223628v93.438647a51.05937 51.05937 0 1 0 100.331662 0V220.450871A219.55529 219.55529 0 0 0 801.887402 0.895581zM51.05937 363.9277a51.05937 51.05937 0 0 0 51.059369-51.05937V220.195574a118.968331 118.968331 0 0 1 116.925957-118.968331h93.693944a51.05937 51.05937 0 0 0 0-100.331662H219.044696A219.55529 219.55529 0 0 0 0 220.195574v93.438647a51.05937 51.05937 0 0 0 51.05937 50.293479zM970.128025 659.306154a51.05937 51.05937 0 0 0-51.05937 51.05937v93.693943a118.968331 118.968331 0 0 1-118.968331 118.713035h-92.162163a51.05937 51.05937 0 1 0 0 100.331661h93.949241A219.299993 219.299993 0 0 0 1021.187395 803.03828v-93.693944a51.05937 51.05937 0 0 0-51.05937-50.038182zM241.510819 358.311169a139.392079 139.392079 0 0 0-106.458786 43.655762 158.028749 158.028749 0 0 0-40.081605 111.564722 149.85925 149.85925 0 0 0 39.315714 106.458786 136.583814 136.583814 0 0 0 102.11874 41.613387 136.839111 136.839111 0 0 0 104.671708-43.145168A157.007562 157.007562 0 0 0 382.945273 507.915123a153.178109 153.178109 0 0 0-38.294527-107.224677A132.243768 132.243768 0 0 0 241.510819 358.311169z m44.676948 214.449353a56.420604 56.420604 0 0 1-47.995807 21.700232 56.420604 56.420604 0 0 1-47.485214-22.466122 102.118739 102.118739 0 0 1-17.870779-60.505353 98.03399 98.03399 0 0 1 18.381373-62.037135 57.697088 57.697088 0 0 1 48.506401-22.976716 55.144119 55.144119 0 0 1 46.97462 22.210826 104.671708 104.671708 0 0 1 17.360186 63.824212 97.012802 97.012802 0 0 1-17.87078 60.250056zM550.930599 358.311169a150.625141 150.625141 0 0 0-111.820019 44.421652 154.70989 154.70989 0 0 0-43.400465 113.096504 144.753313 144.753313 0 0 0 38.294528 105.692895 141.434454 141.434454 0 0 0 105.182301 40.081606 187.898481 187.898481 0 0 0 82.716179-15.828405l4.340047-2.042375v-73.014898l-11.743655 6.893014a124.329565 124.329565 0 0 1-63.824212 16.849592 72.504305 72.504305 0 0 1-56.931198-22.466122 84.758554 84.758554 0 0 1-20.168451-60.505353 86.545632 86.545632 0 0 1 22.210826-62.547728 76.589055 76.589055 0 0 1 59.484166-23.742607 117.947144 117.947144 0 0 1 59.994759 15.573108l11.743655 6.893014v-76.589054l-5.105937-1.787078a198.110355 198.110355 0 0 0-70.972524-10.977765zM872.859926 446.133285c0-37.783934-18.63667-82.971476-107.224677-82.971475h-105.182302v293.336079h74.801977v-107.479974h10.211874a21.444935 21.444935 0 0 1 13.530733 4.850641 93.94924 93.94924 0 0 1 20.168451 25.529684l45.953433 76.589055h86.800928L851.925584 562.548648a146.029797 146.029797 0 0 0-31.146216-37.018043 91.906866 91.906866 0 0 0 33.954481-25.529685 83.226773 83.226773 0 0 0 18.126077-53.867635z m-76.589055 7.403609a32.677997 32.677997 0 0 1-10.211874 25.529685 39.315715 39.315715 0 0 1-28.082653 9.70128h-22.72142v-65.100696h22.466123c33.699184 0 37.27334 16.083701 37.27334 29.869731z"
                        fill="currentColor"
                    />
                  </svg>
                </span>
                文字识别助手
              </button>
            </div>
          </div>
        </header>
        <div
            class="dropzone"
            :class="{ 'has-preview': !!capturedImage }"
            @paste.prevent="handleDropzonePaste"
            @dragover.prevent
            @dragenter.prevent
            @drop.prevent="handleDropzoneDrop"
        >
          <template v-if="capturedImage">
            <img
                :src="capturedImage"
                alt="截图预览"
                class="dropzone-preview"
                :style="previewImageStyle"
                @wheel.prevent="handlePreviewWheel"
                draggable="false"
            />
          </template>
          <template v-else>
            <p class="dropzone-hint">拖放图片 / 粘贴图像</p>
            <p class="dropzone-sub">支持 JPG、PNG、WebP 等常见格式</p>
          </template>
        </div>

        <input
            ref="fileInputRef"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleFileChange"
        />

        <footer class="upload-footer">
          <div class="upload-footer-left">
            <button class="secondary-btn upload-btn" @click="triggerFileSelect">
              <span class="button-icon" aria-hidden="true">
                <svg
                    class="icon"
                    viewBox="0 0 1024 1024"
                    xmlns="http://www.w3.org/2000/svg"
                    role="img"
                >
                  <path
                      d="M128 853.333333h768.064L896 170.666667H127.936L128 853.333333zM127.936 128h768.128C919.594667 128 938.666667 146.986667 938.666667 170.666667v682.666666c0 23.573333-19.029333 42.666667-42.602667 42.666667H127.936A42.56 42.56 0 0 1 85.333333 853.333333V170.666667c0-23.573333 19.029333-42.666667 42.602667-42.666667z m200.128 527.082667c22.890667-19.626667 68.48-36.416 98.794667-36.416h20.949333c40.533333 0 95.914667-20.437333 126.549333-46.698667l52.373334-44.885333c22.890667-19.626667 68.48-36.416 98.794666-36.416H810.666667a21.333333 21.333333 0 0 0 0-42.666667h-85.12c-40.533333 0-95.936 20.437333-126.570667 46.698667l-52.373333 44.885333C523.690667 559.210667 478.165333 576 447.786667 576h-20.949334c-40.490667 0-95.914667 20.437333-126.549333 46.698667L199.445333 709.12a21.333333 21.333333 0 1 0 27.776 32.384l100.842667-86.442667z"
                      fill="currentColor"
                  />
                  <path
                      d="M352 373.333333m-53.333333 0a53.333333 53.333333 0 1 0 106.666666 0 53.333333 53.333333 0 1 0-106.666666 0Z"
                      fill="currentColor"
                  />
                </svg>
              </span>
              选择图片
            </button>
          </div>
          <button class="secondary-btn clear-btn" @click="clearImage" :disabled="!capturedImage">
            <span class="button-icon button-icon--danger" aria-hidden="true">
              <svg
                  class="icon"
                  viewBox="0 0 1024 1024"
                  xmlns="http://www.w3.org/2000/svg"
                  role="img"
              >
                <path
                    d="M926.3 337.9c-22.6-53.3-54.8-101.2-96-142.3-41.1-41.1-89-73.4-142.3-96-55.2-23.4-113.9-35.2-174.3-35.2S394.6 76.2 339.3 99.6c-53.3 22.6-101.2 54.8-142.3 96-41.1 41.1-73.4 89-96 142.3-23.4 55.2-35.2 113.9-35.2 174.3 0 60.4 11.8 119.1 35.2 174.3 22.6 53.3 54.8 101.2 96 142.3 41.1 41.1 89 73.4 142.3 96 55.2 23.4 113.9 35.2 174.3 35.2s119.1-11.8 174.3-35.2c53.3-22.6 101.2-54.8 142.3-96 41.1-41.1 73.4-89 96-142.3 23.4-55.2 35.2-113.9 35.2-174.3 0.1-60.4-11.8-119.1-35.1-174.3zM513.7 879.1c-202.3 0-366.9-164.6-366.9-366.9s164.6-366.9 366.9-366.9c202.3 0 366.9 164.6 366.9 366.9S716 879.1 513.7 879.1z"
                    fill="currentColor"
                />
                <path
                    d="M695.7 555.7c23.6 0 43-19.3 43-43 0-23.6-19.3-43-43-43l-364-0.5c-23.6 0-43 19.3-43 43s19.3 43 43 43l364 0.5z"
                    fill="currentColor"
                />
              </svg>
            </span>
            清空
          </button>
        </footer>
      </section>

      <section class="panel result-panel">
        <header class="result-header">
          <div class="header-left">
            <div class="chip-group">
              <button
                  class="chip"
                  :class="{ 'is-active': activeMode === 'text' }"
                  @click="selectMode('text')"
              >
                <span class="chip-icon" aria-hidden="true">
                  <svg
                      class="icon"
                      viewBox="0 0 1024 1024"
                      xmlns="http://www.w3.org/2000/svg"
                      role="img"
                  >
                    <path
                        d="M555.52 250.624l234.624 521.344h-104.32l-48.384-108.032H399.168l-44.736 108.032H250.24l219.712-521.344H555.52z m-40.96 137.792L438.208 568.96h156.416l-80-180.608z"
                        fill="currentColor"
                    />
                    <path
                        d="M64.128 718.528L64 951.168h232.704v64.128H0v-296.768h64.128z m959.872 0v296.128h-9.344v0.64h-296.128v-64.064l232.64-0.064v-232.64H1024zM1024 0v296.768h-72.768l-0.064-232.704h-232.64V0H1024zM296.768 0v64.128L64.064 64v232.704H0V0h296.768z"
                        fill="currentColor"
                    />
                  </svg>
                </span>
                文字识别
              </button>
              <button
                  class="chip"
                  :class="{ 'is-active': activeMode === 'table' }"
                  @click="selectMode('table')"
              >
                <span class="chip-icon" aria-hidden="true">
                  <svg
                      class="icon"
                      viewBox="0 0 1024 1024"
                      xmlns="http://www.w3.org/2000/svg"
                      role="img"
                  >
                    <path
                        d="M959.825022 384.002258V191.939717C959.825022 121.2479 902.517291 63.940169 831.825474 63.940169H191.939717C121.2479 63.940169 63.940169 121.2479 63.940169 191.939717v639.885757C63.940169 902.517291 121.2479 959.825022 191.939717 959.825022h639.885757c70.691817 0 127.999548-57.307731 127.999548-127.999548V384.002258zM146.66502 146.66502a63.737872 63.737872 0 0 1 45.336109-18.784682h639.997742A63.961844 63.961844 0 0 1 895.884854 192.001129V320.062089H127.880338V192.001129A63.737872 63.737872 0 0 1 146.66502 146.66502z m269.1267 461.308451v-223.971213h192.181751v223.971213h-192.181751z m192.181751 63.940169v223.971214h-192.181751v-223.971214h192.181751z m-256.12192-63.940169H127.880338v-223.971213h223.971213v223.971213z m-205.186531 269.235073a63.466939 63.466939 0 0 1-18.784682-45.209673V671.91364h223.971213v223.971214H192.001129a63.625887 63.625887 0 0 1-45.336109-18.67631z m749.219834-45.209673A63.763159 63.763159 0 0 1 831.998871 895.884854H671.91364v-223.971214h223.971214v160.085231z m0-224.0254h-223.971214v-223.971213h223.971214v223.971213z"
                        fill="currentColor"
                    />
                  </svg>
                </span>
                表格识别
              </button>
              <button
                  class="chip"
                  :class="{ 'is-active': activeMode === 'formula' }"
                  @click="selectMode('formula')"
              >
                <span class="chip-icon" aria-hidden="true">
                  <svg
                      class="icon"
                      viewBox="0 0 1024 1024"
                      xmlns="http://www.w3.org/2000/svg"
                      role="img"
                  >
                    <path
                        d="M512 928H128a32 32 0 0 1-26.88-49.92L345.6 512 101.12 145.92A32 32 0 0 1 128 96h384a32 32 0 0 1 0 64H187.52l223.36 334.08a33.28 33.28 0 0 1 0 35.84L187.52 864H512a32 32 0 0 1 0 64zM640 928a36.48 36.48 0 0 1-17.92-5.12 32.64 32.64 0 0 1-8.96-44.8l256-384a32 32 0 0 1 53.76 35.84l-256 384a33.28 33.28 0 0 1-26.88 14.08z"
                        fill="currentColor"
                    />
                    <path
                        d="M896 928a33.28 33.28 0 0 1-26.88-14.08l-256-384a32 32 0 1 1 53.76-35.84l256 384a32.64 32.64 0 0 1-8.96 44.8 36.48 36.48 0 0 1-17.92 5.12z"
                        fill="currentColor"
                    />
                  </svg>
                </span>
                公式识别
              </button>
              <button
                  class="chip"
                  :class="{ 'is-active': activeMode === 'custom' }"
                  @click="selectMode('custom')"
              >
                <span class="chip-icon" aria-hidden="true">
                  <svg
                      class="icon"
                      viewBox="0 0 1024 1024"
                      xmlns="http://www.w3.org/2000/svg"
                      role="img"
                  >
                    <path
                        d="M911.275224 233.685939 825.637477 319.323686 704.59016 198.276368 790.227906 112.638622C816.453389 86.413139 859.427341 86.80945 886.152521 113.551861L910.379215 137.761325C937.104395 164.503736 937.500707 207.460456 911.275224 233.685939ZM293.012048 609.888941 414.059366 730.919028 232.479774 791.468533 293.012048 609.888941ZM801.393552 343.567611 438.28606 706.692334 317.238743 585.645016 680.363465 222.520293 801.393552 343.567611ZM179.666958 189.953826C151.166992 189.953826 127.974154 213.146665 127.974154 241.646631L127.974154 844.798277C127.974154 873.298243 151.166992 896.491082 179.666958 896.491082L782.818604 896.491082C811.31857 896.491082 834.511409 873.298243 834.511409 844.798277L834.511409 360.540082 868.973279 326.078212 868.973279 844.798277C868.973279 892.303964 830.324292 930.952951 782.818604 930.952951L179.666958 930.952951C132.161271 930.952951 93.512284 892.303964 93.512284 844.798277L93.512284 241.646631C93.512284 194.140944 132.161271 155.491957 179.666958 155.491957L698.387023 155.491957 663.925153 189.953826 179.666958 189.953826Z"
                        fill="currentColor"
                    />
                  </svg>
                </span>
                自定义
              </button>
            </div>
          </div>
          <div v-if="activeMode === 'custom'" class="custom-instruction-selector">
            <a-select
                v-model:value="selectedInstructionId"
                placeholder="选择指令"
                style="width: 130px"
                @change="handleInstructionChange"
            >
              <template #dropdownRender="{ menuNode }">
                <div class="custom-instruction-dropdown">
                  <component :is="menuNode" />
                  <div class="custom-instruction-add" @mousedown.prevent @click="showAddInstructionModal = true">
                    <span class="custom-instruction-add-inner" aria-label="添加自定义指令">
                      <svg class="add-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
                        <path d="M512 0c-285.257143 0-512 226.742857-512 512s226.742857 512 512 512 512-226.742857 512-512-226.742857-512-512-512z m0 950.857143c-241.371429 0-438.857143-197.485714-438.857143-438.857143s197.485714-438.857143 438.857143-438.857143 438.857143 197.485714 438.857143 438.857143-197.485714 438.857143-438.857143 438.857143z" fill="currentColor"></path>
                        <path d="M731.428571 475.428571h-182.857142v-182.857142c0-21.942857-14.628571-36.571429-36.571429-36.571429s-36.571429 14.628571-36.571429 36.571429v182.857142h-182.857142c-21.942857 0-36.571429 14.628571-36.571429 36.571429s14.628571 36.571429 36.571429 36.571429h182.857142v182.857142c0 21.942857 14.628571 36.571429 36.571429 36.571429s36.571429-14.628571 36.571429-36.571429v-182.857142h182.857142c21.942857 0 36.571429-14.628571 36.571429-36.571429s-14.628571-36.571429-36.571429-36.571429z" fill="currentColor"></path>
                      </svg>
                    </span>
                  </div>
                </div>
              </template>
              <a-select-option
                  v-for="instruction in predefinedInstructions"
                  :key="instruction.id"
                  :value="instruction.id"
              >
                <div class="instruction-option">
                  <span>{{ instruction.name }}</span>
                  <svg
                      v-if="instruction.id.startsWith('custom-')"
                      class="delete-instruction-icon"
                      viewBox="0 0 1024 1024"
                      xmlns="http://www.w3.org/2000/svg"
                      @click.stop="handleDeleteInstruction(instruction.id)"
                  >
                    <path
                        d="M512 466.944l233.472-233.472a31.744 31.744 0 0 1 45.056 45.056L557.056 512l233.472 233.472a31.744 31.744 0 0 1-45.056 45.056L512 557.056l-233.472 233.472a31.744 31.744 0 0 1-45.056-45.056L466.944 512 233.472 278.528a31.744 31.744 0 0 1 45.056-45.056z"
                        fill="#5A5A68"
                    />
                  </svg>
                </div>
              </a-select-option>
            </a-select>
          </div>
        </header>

        <div class="result-body">
          <div v-if="ocrError" class="result-error">
            <p>{{ ocrError }}</p>
          </div>
          <div v-else-if="displayResult" class="result-text-wrapper">
            <div v-if="isProcessing" class="result-loading-inline">
              <span></span>
              <span></span>
              <span></span>
            </div>
            <textarea
                v-model="processedResult"
                class="result-text editable"
                spellcheck="false"
            ></textarea>
          </div>
          <div v-else-if="isUploading">
            <a-space>
              <a-spin size="large"/>
            </a-space>
          </div>
          <div v-else class="result-placeholder">
            <div class="placeholder-icon">Tt</div>
            <p>{{ placeholderText }}</p>
          </div>
        </div>

        <footer class="result-footer">
          <button
              v-if="activeMode === 'text'"
              class="secondary-btn audio-footer-btn"
              type="button"
              :disabled="!audioUrl"
              @click="handlePlayAudio"
          >
            <span class="audio-icon-wrapper">
              <svg
                  v-if="!isPlayingAudio"
                  class="icon"
                  viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg"
                  aria-hidden="true"
              >
                <path
                    fill="currentColor"
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M5.714 8.857h.8l.596-.532 4.366-3.899v15.148L7.11 15.675l-.596-.532H3.095V8.857zm0-2.095 5.295-4.728c1.027-.834 2.562-.103 2.562 1.22v17.492c0 1.324-1.535 2.054-2.562 1.22l-5.295-4.728H3.095A2.095 2.095 0 0 1 1 15.143V8.857c0-1.157.938-2.095 2.095-2.095zM18.03 4.274a1.05 1.05 0 0 1 1.48-.082A10.45 10.45 0 0 1 23 12c0 3.103-1.35 5.892-3.492 7.809a1.048 1.048 0 0 1-1.397-1.562A8.36 8.36 0 0 0 20.905 12a8.36 8.36 0 0 0-2.794-6.247 1.05 1.05 0 0 1-.082-1.48m-.5 3.924a1.048 1.048 0 0 0-1.63 1.318c.606.748.932 1.518.932 2.484 0 .967-.326 1.736-.931 2.484a1.048 1.048 0 0 0 1.629 1.318c.85-1.052 1.397-2.274 1.397-3.802s-.546-2.75-1.397-3.802"
                />
              </svg>
              <div v-else class="audio-wave">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
              </div>
            </span>
            朗读
          </button>

          <label
              v-if="activeMode === 'text'"
              class="vision-model-toggle toggle-newlines-btn"
          >
            <input
                type="checkbox"
                v-model="removeNewlines"
            />
            <span class="toggle-label">
              去除换行符
            </span>
          </label>

          <label class="vision-model-toggle">
            <input 
                type="checkbox" 
                v-model="useVisionModel"
                :disabled="isProcessing || isTesseractProcessing"
            />
            <span class="toggle-label">AI 识别</span>
          </label>

          <div class="footer-actions">
            <button class="secondary-btn icon-btn" :disabled="!displayResult" @click="handleCopy">
              <span class="button-icon" aria-hidden="true">
                <svg
                    class="icon"
                    viewBox="0 0 1024 1024"
                    xmlns="http://www.w3.org/2000/svg"
                    role="img"
                >
                  <path
                      d="M720 192h-544A80.096 80.096 0 0 0 96 272v608C96 924.128 131.904 960 176 960h544c44.128 0 80-35.872 80-80v-608C800 227.904 764.128 192 720 192z m16 688c0 8.8-7.2 16-16 16h-544a16 16 0 0 1-16-16v-608a16 16 0 0 1 16-16h544a16 16 0 0 1 16 16v608z"
                      fill="currentColor"
                  />
                  <path
                      d="M848 64h-544a32 32 0 0 0 0 64h544a16 16 0 0 1 16 16v608a32 32 0 1 0 64 0v-608C928 99.904 892.128 64 848 64z"
                      fill="currentColor"
                  />
                  <path
                      d="M608 360H288a32 32 0 0 0 0 64h320a32 32 0 1 0 0-64zM608 520H288a32 32 0 1 0 0 64h320a32 32 0 1 0 0-64zM480 678.656H288a32 32 0 1 0 0 64h192a32 32 0 1 0 0-64z"
                      fill="currentColor"
                  />
                </svg>
              </span>
              复制结果
            </button>
            <button class="secondary-btn icon-btn" :disabled="!displayResult" @click="openTranslate">
              <span class="button-icon" aria-hidden="true">
                <svg
                    class="icon"
                    viewBox="0 0 1024 1024"
                    xmlns="http://www.w3.org/2000/svg"
                    role="img"
                >
                  <path
                      d="M414.254545 595.781818H172.218182c-86.109091 0-155.927273-67.490909-155.927273-148.945454V218.763636C16.290909 137.309091 86.109091 69.818182 172.218182 69.818182h242.036363c86.109091 0 155.927273 67.490909 155.927273 148.945454v228.072728c-2.327273 81.454545-69.818182 148.945455-155.927273 148.945454zM172.218182 137.309091c-48.872727 0-86.109091 34.909091-86.109091 81.454545v228.072728c0 44.218182 39.563636 81.454545 86.109091 81.454545h242.036363c48.872727 0 86.109091-34.909091 86.109091-81.454545V218.763636c0-44.218182-39.563636-81.454545-86.109091-81.454545H172.218182z"
                      fill="currentColor"
                  />
                  <path
                      d="M837.818182 861.090909H595.781818c-90.763636 0-155.927273-69.818182-155.927273-167.563636v-141.963637c0-18.618182 16.290909-34.909091 34.909091-34.909091s34.909091 16.290909 34.909091 34.909091v141.963637c0 58.181818 34.909091 100.072727 86.109091 100.072727H837.818182c48.872727 0 86.109091-34.909091 86.109091-81.454545v-228.072728c0-44.218182-39.563636-81.454545-86.109091-81.454545H544.581818c-18.618182 0-34.909091-16.290909-34.909091-34.909091s16.290909-34.909091 34.909091-34.909091H837.818182c86.109091 0 155.927273 67.490909 155.927273 148.945455v228.072727c0 86.109091-69.818182 151.272727-155.927273 151.272727zM262.981818 847.127273c-102.4 0-183.854545-74.472727-183.854545-167.563637 0-18.618182 16.290909-34.909091 34.909091-34.909091s34.909091 16.290909 34.909091 34.909091c0 55.854545 51.2 100.072727 116.363636 100.072728 18.618182 0 34.909091 16.290909 34.909091 34.909091-4.654545 18.618182-18.618182 32.581818-37.236364 32.581818zM861.090909 281.6c-18.618182 0-34.909091-16.290909-34.909091-34.909091 0-55.854545-51.2-100.072727-116.363636-100.072727-18.618182 0-34.909091-16.290909-34.909091-34.909091s16.290909-34.909091 34.909091-34.909091c102.4 0 183.854545 74.472727 183.854545 167.563636 2.327273 20.945455-11.636364 37.236364-32.581818 37.236364z"
                      fill="currentColor"
                  />
                  <path
                      d="M660.945455 686.545455h-39.563637l88.436364-165.236364h41.890909l88.436364 165.236364h-41.89091l-23.272727-46.545455h-93.090909l-20.945454 46.545455z m69.818181-139.636364l-37.236363 72.145454H768l-37.236364-72.145454z"
                      fill="currentColor"
                  />
                  <path
                      d="M286.254545 200.145455h23.272728v39.563636H395.636364V349.090909h-23.272728v-13.963636h-62.836363v76.8h-23.272728v-76.8H223.418182v13.963636h-23.272727v-109.381818h86.10909V200.145455z m-62.836363 116.363636h62.836363v-55.854546H223.418182v55.854546z m86.109091 0H372.363636v-55.854546h-62.836363v55.854546z"
                      fill="currentColor"
                  />
                </svg>
              </span>
              前往翻译
            </button>
          </div>
        </footer>
      </section>
    </div>

    <!-- 添加自定义指令模态框 -->
    <a-modal
        v-model:visible="showAddInstructionModal"
        title="添加自定义指令"
        ok-text="添加"
        cancel-text="取消"
        @ok="handleAddInstruction"
    >
      <a-form layout="vertical">
        <a-form-item label="指令名称">
          <a-input
              v-model:value="newInstructionName"
              placeholder="例如：提取收据信息"
          />
        </a-form-item>
        <a-form-item label="提示词">
          <a-textarea
              v-model:value="newInstructionPrompt"
              placeholder="例如：从图片中提取收据的所有信息，包括商家名称、金额、日期等"
              :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import {computed, onBeforeUnmount, onMounted, ref, watch} from 'vue'
import { message as antMessage } from 'ant-design-vue'
import {streamChat} from '@/api/chat'
import Tesseract from 'tesseract.js'

const OCR_APP_ID = import.meta.env.VITE_APP_ID as string | undefined
const USER_ID = import.meta.env.VITE_USER_ID || 'default-user'

type RecognitionMode = 'text' | 'table' | 'formula' | 'custom'
type RecognitionScheme = 'tesseract' | 'vision-model'

interface CustomInstruction {
  id: string
  name: string
  prompt: string
}

const handlePreviewWheel = (event: WheelEvent) => {
  if (!capturedImage.value) return
  event.preventDefault()
  const delta = event.deltaY
  const step = delta > 0 ? -0.1 : 0.1
  const nextScale = previewScale.value + step
  previewScale.value = Math.min(Math.max(nextScale, 0.5), 3)
}

const PROMPTS: Record<Exclude<RecognitionMode, 'custom'>, string> = {
  text: '提取这张图片上的文字',
  table: '提取图片中的表格内容，使用 Markdown 表格输出结果',
  formula: '提取图片上的公式，输出对应的LaTeX代码，使用斜杠换行',
}

const PREDEFINED_INSTRUCTIONS: CustomInstruction[] = [
  {
    id: 'personal-info',
    name: '提取个人信息',
    prompt: '从图片中提取用户的个人信息，包括姓名、性别、年龄、身份证号、联系方式等，以结构化格式输出',
  },
  {
    id: 'contact-info',
    name: '提取联系方式',
    prompt: '从图片中提取所有联系方式，包括电话号码、邮箱、地址、微信号、QQ号等',
  },
  {
    id: 'financial-info',
    name: '提取财务信息',
    prompt: '从图片中提取财务相关信息，包括金额、账户、交易记录、发票号等',
  },
  {
    id: 'product-info',
    name: '提取产品信息',
    prompt: '从图片中提取产品相关信息，包括产品名称、规格、价格、生产日期、有效期等',
  },
  {
    id: 'document-info',
    name: '提取文档信息',
    prompt: '从图片中提取文档的关键信息，包括标题、日期、作者、版本号等元数据',
  },
]

const ocrPageRef = ref<HTMLElement | null>(null)
const capturedImage = ref<string | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const isProcessing = ref(false)
const isUploading = ref(false)
const ocrError = ref<string | null>(null)
const rawResult = ref('')
const removeNewlines = ref(false)
const audioUrl = ref('')
const isPlayingAudio = ref(false)
let audioInstance: HTMLAudioElement | null = null
// 去除换行功能已移除，保留结果原始格式
const streamAbort = ref<(() => void) | null>(null)
const activeMode = ref<RecognitionMode>('text')
const lastUploadedFile = ref<File | null>(null)
const selectedInstructionId = ref<string>('')
const predefinedInstructions = ref<CustomInstruction[]>(PREDEFINED_INSTRUCTIONS)
const showAddInstructionModal = ref(false)
const newInstructionName = ref('')
const newInstructionPrompt = ref('')
const previewScale = ref(1)

// 识别方案管理
const currentScheme = ref<RecognitionScheme>('tesseract')  // 默认使用 Tesseract.js
const tesseractResult = ref('')  // 存储 Tesseract 识别结果
const isTesseractProcessing = ref(false)  // Tesseract 处理中
const useVisionModel = ref(false)  // 是否使用大模型识别开关

const processedResult = computed({
  get() {
    if (!rawResult.value) return ''
    return removeNewlines.value
        ? rawResult.value.replace(/\r?\n/g, '')
        : rawResult.value
  },
  set(value: string) {
    rawResult.value = value
  },
})

const displayResult = computed(() => {
  return processedResult.value
})

const previewImageStyle = computed(() => ({
  transform: `scale(${previewScale.value})`,
  transformOrigin: 'center',
}))

const PLACEHOLDER_COPY: Record<Exclude<RecognitionMode, 'custom'>, string> = {
  text: 'OCR 文字识别结果',
  table: 'OCR 表格识别结果',
  formula: 'OCR 公式识别结果',
}

const placeholderText = computed(() => {
  if (activeMode.value === 'custom') {
    const instruction = predefinedInstructions.value.find(i => i.id === selectedInstructionId.value)
    return instruction ? `自定义指令：${instruction.name}` : '请选择或添加自定义指令'
  }
  return PLACEHOLDER_COPY[activeMode.value]
})

const currentPrompt = computed(() => {
  if (activeMode.value === 'custom') {
    const instruction = predefinedInstructions.value.find(i => i.id === selectedInstructionId.value)
    return instruction?.prompt || ''
  }
  return PROMPTS[activeMode.value]
})

const clearImage = () => {
  capturedImage.value = null
  rawResult.value = ''
  audioUrl.value = ''
  isPlayingAudio.value = false
  if (audioInstance) {
    audioInstance.pause()
    audioInstance.currentTime = 0
    audioInstance = null
  }
  ocrError.value = null
  streamAbort.value?.()
  streamAbort.value = null
  isProcessing.value = false
  isUploading.value = false
  previewScale.value = 1
}

const triggerFileSelect = () => {
  fileInputRef.value?.click()
}

const fileToDataUrl = (file: File) => {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = () => reject(reader.error)
    reader.readAsDataURL(file)
  })
}

const handleDropzonePaste = async (event: ClipboardEvent) => {
  const items = event.clipboardData?.items
  if (!items || items.length === 0) return

  for (let i = 0; i < items.length; i += 1) {
    const item = items[i]
    if (!item) continue
    if (item.type.startsWith('image/')) {
      const file = item.getAsFile()
      if (!file) continue
      try {
        event.preventDefault()
        isUploading.value = true
        capturedImage.value = await fileToDataUrl(file)
        lastUploadedFile.value = file
        await startRecognition(file)
      } catch (error) {
        console.error('粘贴图片处理失败', error)
        ocrError.value = '粘贴图片处理失败，请重试'
        isUploading.value = false
      }
      break
    }
  }
}

const handleDropzoneDrop = async (event: DragEvent) => {
  const files = event.dataTransfer?.files
  if (!files || files.length === 0) return

  const file = files[0]
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ocrError.value = '请拖入图片文件'
    return
  }

  try {
    isUploading.value = true
    capturedImage.value = await fileToDataUrl(file)
    lastUploadedFile.value = file
    await startRecognition(file)
  } catch (error) {
    console.error('拖放图片处理失败', error)
    ocrError.value = '拖放图片处理失败，请重试'
    isUploading.value = false
  }
}

const dataUrlToFile = (dataUrl: string, fileName: string) => {
  const [meta, content] = dataUrl.split(',')
  const mimeMatch = meta?.match(/data:(.*?);/)
  const mime = mimeMatch ? mimeMatch[1] : 'image/png'
  const binary = atob(content || '')
  const len = binary.length
  const u8arr = new Uint8Array(len)
  for (let i = 0; i < len; i += 1) {
    u8arr[i] = binary.charCodeAt(i)
  }
  return new File([u8arr], fileName, {type: mime})
}

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement | null
  const file = target?.files?.[0]
  if (!file) return
  try {
    isUploading.value = true
    capturedImage.value = await fileToDataUrl(file)
    lastUploadedFile.value = file
    await startRecognition(file)
  } catch (error) {
    console.error('读取图片失败', error)
    ocrError.value = '读取图片失败，请重试'
    isUploading.value = false
  } finally {
    if (target) {
      target.value = ''
    }
  }
}

// Tesseract.js 本地识别（方案1）
const recognizeWithTesseract = async (file: File) => {
  try {
    isTesseractProcessing.value = true
    ocrError.value = null
    rawResult.value = ''
    tesseractResult.value = ''
    isUploading.value = false

    const reader = new FileReader()
    reader.onload = async (e) => {
      try {
        const result = await Tesseract.recognize(
            e.target?.result as string,
            'chi_sim+eng',  // 中文简体 + 英文
            {
              logger: (m) => {
                // 可选：显示进度
                // console.log('Tesseract 进度:', m)
              }
            }
        )
        
        tesseractResult.value = result.data.text
        rawResult.value = result.data.text
        isTesseractProcessing.value = false
      } catch (error) {
        console.error('Tesseract 识别失败', error)
        ocrError.value = 'Tesseract 识别失败，请尝试使用大模型识别'
        isTesseractProcessing.value = false
      }
    }
    reader.readAsDataURL(file)
  } catch (error) {
    console.error('Tesseract 初始化失败', error)
    ocrError.value = '本地识别初始化失败'
    isTesseractProcessing.value = false
  }
}

// 视觉大模型识别（方案2）
const recognizeWithVisionModel = async (file: File) => {
  if (!OCR_APP_ID) {
    ocrError.value = '缺少 OCR 应用配置，请检查环境变量'
    return
  }

  streamAbort.value?.()
  rawResult.value = ''
  ocrError.value = null
  isProcessing.value = true

  try {
    streamAbort.value = await streamChat(
        {
          appId: OCR_APP_ID,
          query: currentPrompt.value,
          userId: USER_ID,
          file,
        },
        (event) => {
          const value = typeof event.data === 'string' ? event.data : event.data?.v ?? ''
          if (typeof value !== 'string' || !value) return

          const trimmed = value.trim()
          const isUrl = /^https?:\/\//i.test(trimmed)

          if (isUrl) {
            audioUrl.value = trimmed
            isPlayingAudio.value = false
            if (audioInstance) {
              audioInstance.pause()
              audioInstance.currentTime = 0
              audioInstance = null
            }
            return
          }

          rawResult.value += value
          isUploading.value = false
        },
        (error) => {
          console.error('OCR 请求失败', error)
          ocrError.value = '视觉大模型识别失败，请稍后重试'
          isProcessing.value = false
          isUploading.value = false
          streamAbort.value = null
        },
        () => {
          isProcessing.value = false
          isUploading.value = false
          streamAbort.value = null
          if (!rawResult.value && !ocrError.value) {
            ocrError.value = '未获取到识别结果'
          }
        },
        {endpoint: '/api/bailing/stream-chat-with-file'},
    )
  } catch (error) {
    console.error('OCR 请求启动失败', error)
    ocrError.value = '无法发起识别请求，请稍后再试'
    isProcessing.value = false
    streamAbort.value = null
  }
}

const startRecognition = async (file: File) => {
  lastUploadedFile.value = file
  
  // 根据开关选择识别方案
  if (useVisionModel.value) {
    await recognizeWithVisionModel(file)
  } else {
    await recognizeWithTesseract(file)
  }
}
const copyResult = async () => {
  if (!displayResult.value) return
  try {
    await copyTextToClipboard(displayResult.value)
    antMessage.success('已复制到剪贴板')
  } catch (error) {
    console.error('复制失败', error)
    ocrError.value = '复制失败，请手动选择文本'
  }
}

const handleCopy = async () => {
  await copyResult()
}

const openTranslate = () => {
  if (!displayResult.value) return
  const url = `https://fanyi.baidu.com/#zh/en/${encodeURIComponent(displayResult.value)}`
  window.open(url, '_blank', 'noopener')
}

const copyTextToClipboard = async (text: string) => {
  if (!text) return

  if (navigator.clipboard && navigator.clipboard.writeText) {
    await navigator.clipboard.writeText(text)
    return
  }

  const textarea = document.createElement('textarea')
  textarea.value = text
  textarea.style.position = 'fixed'
  textarea.style.opacity = '0'
  textarea.style.left = '-9999px'
  document.body.appendChild(textarea)
  textarea.select()
  try {
    document.execCommand('copy')
  } finally {
    document.body.removeChild(textarea)
  }
}

const selectMode = async (mode: RecognitionMode) => {
  if (activeMode.value === mode) return

  // 切换模式时仅更新当前模式，并清空结果区，避免自动重新发起请求
  activeMode.value = mode
  rawResult.value = ''
  audioUrl.value = ''
  isPlayingAudio.value = false
  ocrError.value = null
  
  // 如果切换到自定义模式，默认选择第一条指令
  if (mode === 'custom') {
    selectedInstructionId.value = predefinedInstructions.value[0]?.id || ''
  }
}

const handleInstructionChange = (value: string) => {
  selectedInstructionId.value = value
}

const handleAddInstruction = () => {
  if (!newInstructionName.value.trim() || !newInstructionPrompt.value.trim()) {
    return
  }

  const newInstruction: CustomInstruction = {
    id: `custom-${Date.now()}`,
    name: newInstructionName.value,
    prompt: newInstructionPrompt.value,
  }

  predefinedInstructions.value.push(newInstruction)
  selectedInstructionId.value = newInstruction.id

  // 重置表单并关闭模态框
  newInstructionName.value = ''
  newInstructionPrompt.value = ''
  showAddInstructionModal.value = false
}

const handleDeleteInstruction = (instructionId: string) => {
  const index = predefinedInstructions.value.findIndex(i => i.id === instructionId)
  if (index > -1) {
    predefinedInstructions.value.splice(index, 1)
    // 如果删除的是当前选中的指令，重置选择
    if (selectedInstructionId.value === instructionId) {
      selectedInstructionId.value = predefinedInstructions.value[0]?.id || ''
    }
  }
}

const handlePlayAudio = () => {
  if (!audioUrl.value) return
  try {
    // 如果已有实例且在播放，则点击变为暂停
    if (audioInstance && isPlayingAudio.value) {
      audioInstance.pause()
      isPlayingAudio.value = false
      return
    }

    // 如果已有实例但未播放，只需复用
    if (!audioInstance) {
      audioInstance = new Audio(audioUrl.value)
      audioInstance.addEventListener('ended', () => {
        isPlayingAudio.value = false
      })
    }

    void audioInstance.play()
    isPlayingAudio.value = true
  } catch (error) {
    console.error('播放音频失败', error)
    isPlayingAudio.value = false
  }
}

onMounted(() => {
})

onBeforeUnmount(() => {
  streamAbort.value?.()
  streamAbort.value = null
  if (audioInstance) {
    audioInstance.pause()
    audioInstance = null
  }
})
</script>

<style scoped>
/* 直接复用 Ocr.vue 的样式 */
:global(body) {
  background-color: #ffffff;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #0f172a;
}

.ocr-page {
  height: 100%;
  color: #0f172a;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.ocr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 18px;
}

.header-tabs {
  display: inline-flex;
  padding: 4px;
  border-radius: 999px;
  background-color: #e0e7ff;
  gap: 6px;
}

.tab {
  border: none;
  border-radius: 999px;
  padding: 8px 18px;
  background: transparent;
  color: #4b5563;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.tab:hover {
  color: #111827;
}

.tab.is-active {
  background: #ffffff;
  color: #111827;
  font-weight: 600;
}

.mode-chip svg {
  width: 20px;
  height: 20px;
}

.tab-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}

.tab-icon .icon {
  width: 100%;
  height: 100%;
}

.chip-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
}

.chip-icon .icon {
  width: 100%;
  height: 100%;
}

.ocr-content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 16px;
  align-items: stretch;
}

.panel {
  background: #ffffff;
  border-radius: 24px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  padding: 16px 18px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.upload-panel {
  gap: 12px;
  flex: 1;
  min-height: 0;
}

.dropzone {
  border-radius: 16px;
  padding: 12px 16px;
  text-align: center;
  background: #f8fafc;
  flex: 1 1 0;
  min-height: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
  overflow: hidden;
}

.dropzone.has-preview {
  padding: 8px 12px;
}

.dropzone-preview {
  width: 100%;
  height: auto;
  max-height: 100%;
  max-width: 100%;
  flex: 0 0 auto;
  border-radius: 18px;
  object-fit: contain;
  background: #f8fafc;
  transition: transform 0.2s ease;
  cursor: grab;
}

.dropzone-icon svg {
  width: 38px;
  height: 38px;
}

.dropzone-hint {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #0f172a;
}

.dropzone-sub {
  font-size: 14px;
  color: rgba(15, 23, 42, 0.5);
}

.upload-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  height: 56px;
  margin-top: 8px;
}

.upload-footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.button-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  margin-right: 6px;
}

.button-icon .icon {
  width: 100%;
  height: 100%;
}

.button-icon--danger {
  color: #d81e06;
}

.upload-btn {
  display: inline-flex;
  align-items: center;
}

.upload-btn svg {
  width: 20px;
  height: 20px;
}

.clear-btn {
  display: inline-flex;
  align-items: center;
  background: rgba(239, 68, 68, 0.08);
  color: #b91c1c;
}

.clear-btn:hover {
  background: rgba(239, 68, 68, 0.16);
}

.result-panel {
  gap: 12px;
  flex: 1;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  gap: 16px;
}

.custom-instruction-selector {
  display: flex;
  align-items: center;
}

.instruction-option {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 8px;
}

.instruction-option span {
  flex: 1;
  text-align: center;
}

.delete-instruction-icon {
  width: 16px;
  height: 16px;
  cursor: pointer;
  flex-shrink: 0;
  transition: opacity 0.2s;
}

.delete-instruction-icon:hover {
  opacity: 0.7;
}

.custom-instruction-add {
  display: flex;
  justify-content: center;
}

.custom-instruction-add-inner {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 999px;
  color: #76a9e3;
}

.custom-instruction-add-inner:hover {
  color: #4d8e
}

.chip-group {
  display: inline-flex;
  gap: 12px;
  background: rgba(59, 130, 246, 0.1);
  padding: 6px;
  border-radius: 999px;
}

.chip {
  border: none;
  border-radius: 999px;
  padding: 8px 18px;
  background: transparent;
  color: #4b5563;
  cursor: pointer;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.chip.is-active {
  background: #ffffff;
  color: #111827;
  font-weight: 600;
}

.language-select svg {
  width: 18px;
  height: 18px;
}

.result-body {
  flex: 1;
  border-radius: 16px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 12px 16px;
}

.result-loading p {
  margin: 0;
  font-size: 14px;
}

.result-loading p::after {
  content: '';
  display: inline-block;
  width: 1.2em;
  text-align: left;
  animation: loading-dots 1.2s steps(4, end) infinite;
}

.result-placeholder {
  color: rgba(15, 23, 42, 0.55);
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}

.placeholder-icon {
  font-size: 52px;
  font-weight: 600;
  letter-spacing: 0.05em;
  color: rgba(15, 23, 42, 0.35);
}

.result-text-wrapper {
  position: relative;
  flex: 1;
  width: 100%;
  height: 100%;
  border-radius: 18px;
  background: #f8fafc;
  padding: 0;
  text-align: left;
  overflow: hidden;
}

.result-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
  color: #0f172a;
}

.result-text.editable {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  padding: 16px 20px 48px;
  border: none;
  outline: none;
  resize: none;
  background: transparent;
  overflow: hidden;
}

.result-text.editable::-webkit-scrollbar {
  width: 0;
  height: 0;
}

.audio-footer-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  box-shadow: none;
  padding-left: 0;
  padding-right: 0;
}

.toggle-newlines-btn {
  margin-left: 8px;
}

.result-options {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 12px;
}

.result-option-label {
  user-select: none;
}

.audio-icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
}

.audio-footer-btn .icon {
  width: 18px;
  height: 18px;
}

.audio-wave {
  display: inline-flex;
  align-items: flex-end;
  justify-content: center;
  width: 18px;
  gap: 2px;
  height: 18px;
}

.audio-wave span {
  width: 1.5px;
  background-color: currentColor;
  border-radius: 999px;
  animation: audio-wave 1s infinite ease-in-out;
}

.audio-wave span:nth-child(1) {
  height: 6px;
  animation-delay: 0s;
}

.audio-wave span:nth-child(2) {
  height: 10px;
  animation-delay: 0.1s;
}

.audio-wave span:nth-child(3) {
  height: 14px;
  animation-delay: 0.2s;
}

.audio-wave span:nth-child(4) {
  height: 9px;
  animation-delay: 0.3s;
}

@keyframes audio-wave {
  0%, 100% {
    transform: scaleY(0.6);
    opacity: 0.6;
  }
  50% {
    transform: scaleY(1);
    opacity: 1;
  }
}

@keyframes ocr-spinner {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes loading-dots {
  0% {
    content: '';
  }
  25% {
    content: '.';
  }
  50% {
    content: '..';
  }
  75% {
    content: '...';
  }
  100% {
    content: '';
  }
}

/* 大模型识别开关 */
.vision-model-toggle {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-left: 8px;
  cursor: pointer;
  user-select: none;
}

.vision-model-toggle input[type="checkbox"] {
  appearance: none;
  -webkit-appearance: none;
  width: 40px;
  height: 24px;
  background: #e5e7eb;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  transition: background 0.3s ease;
  flex-shrink: 0;
}

.vision-model-toggle input[type="checkbox"]:checked {
  background: #3b82f6;
}

.vision-model-toggle input[type="checkbox"]::before {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: white;
  top: 2px;
  left: 2px;
  transition: left 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.vision-model-toggle input[type="checkbox"]:checked::before {
  left: 18px;
}

.vision-model-toggle input[type="checkbox"]:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.vision-model-toggle .toggle-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #374151;
  transition: color 0.2s ease;
}

.vision-model-toggle input[type="checkbox"]:checked + .toggle-label {
  color: #3b82f6;
  font-weight: 500;
}

.vision-model-toggle .icon {
  width: 16px;
  height: 16px;
}

@keyframes ocr-upload-indeterminate {
  0% {
    transform: translateX(0%);
  }
  50% {
    transform: translateX(150%);
  }
  100% {
    transform: translateX(300%);
  }
}

@keyframes ocr-inline-dots {
  0%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  50% {
    transform: translateY(-3px);
    opacity: 1;
  }
}

.upload-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.upload-footer .secondary-btn,
.upload-footer .primary-btn {
  background: transparent;
  border: none;
  box-shadow: none;
  padding-left: 0;
  padding-right: 0;
}

.upload-footer .secondary-btn:hover,
.upload-footer .primary-btn:hover {
  background: transparent;
}

.result-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0 0;
  height: 56px;
}

.result-footer .secondary-btn,
.result-footer .primary-btn {
  background: transparent;
  border: none;
  box-shadow: none;
  padding-left: 0;
  padding-right: 0;
}

.result-footer .secondary-btn:hover,
.result-footer .primary-btn:hover {
  background: transparent;
}

.footer-actions {
  display: flex;
  gap: 12px;
  margin-left: auto;
}

.icon-btn {
  display: inline-flex;
  align-items: center;
}

.secondary-btn,
.primary-btn {
  border: none;
  border-radius: 16px;
  padding: 12px 22px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 600;
}

.secondary-btn {
  background: rgba(59, 130, 246, 0.1);
  color: #0f172a;
}

.secondary-btn:hover {
  background: rgba(59, 130, 246, 0.2);
}

.primary-btn {
  background: linear-gradient(135deg, #2563eb, #5de0ff);
  color: #020205;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.25);
}

.primary-btn:hover {
  filter: brightness(1.05);
}

@media (max-width: 768px) {
  .ocr-page {
    padding: 24px;
  }

  .result-footer {
    flex-direction: column;
    gap: 18px;
    align-items: flex-start;
  }
}
</style>
