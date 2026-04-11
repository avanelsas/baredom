(ns baredom.components.x-neural-glow.model)

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-neural-glow")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-orb-count           "orb-count")
(def attr-color-primary       "color-primary")
(def attr-color-secondary     "color-secondary")
(def attr-color-background    "color-background")
(def attr-pulse-speed         "pulse-speed")
(def attr-rest-rate           "rest-rate")
(def attr-connection-distance "connection-distance")
(def attr-orb-size            "orb-size")
(def attr-opacity             "opacity")
(def attr-interactive         "interactive")

(def observed-attributes
  #js [attr-orb-count attr-color-primary attr-color-secondary
       attr-color-background attr-pulse-speed attr-rest-rate
       attr-connection-distance attr-orb-size attr-opacity
       attr-interactive])

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-z-index    "--x-neural-glow-z-index")
(def css-opacity    "--x-neural-glow-opacity")
(def css-blend-mode "--x-neural-glow-blend-mode")
(def css-inset      "--x-neural-glow-inset")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:orbCount           {:type 'number}
   :colorPrimary       {:type 'string}
   :colorSecondary     {:type 'string}
   :colorBackground    {:type 'string}
   :pulseSpeed         {:type 'number}
   :restRate           {:type 'number}
   :connectionDistance  {:type 'number}
   :orbSize            {:type 'number}
   :opacity            {:type 'number}
   :interactive        {:type 'boolean}})

(def event-schema {})

;; ── Defaults ────────────────────────────────────────────────────────────────
(def ^:private default-orb-count           15)
(def ^:private default-color-primary       "#4f8bff")
(def ^:private default-color-secondary     "#00e5cc")
(def ^:private default-color-background    "#050a18")
(def ^:private default-pulse-speed         1.0)
(def ^:private default-rest-rate           4.0)
(def ^:private default-connection-distance 0.15)
(def ^:private default-orb-size            40.0)
(def ^:private default-opacity             0.8)

;; ── Parse functions ─────────────────────────────────────────────────────────
(defn parse-orb-count
  "Parse orb-count to int in [3, 50], default 15."
  [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (or (js/isNaN n) (not (pos? n)))
        default-orb-count
        (js/Math.min 50 (js/Math.max 3 n))))
    default-orb-count))

(defn parse-color
  "Parse a CSS color string. Any non-empty string passes through."
  [s default]
  (if (and (string? s) (pos? (.-length (.trim s))))
    (.trim s)
    default))

(defn parse-color-primary [s]
  (parse-color s default-color-primary))

(defn parse-color-secondary [s]
  (parse-color s default-color-secondary))

(defn parse-color-background [s]
  (parse-color s default-color-background))

(defn parse-pulse-speed
  "Parse pulse-speed to float in [0.1, 5.0], default 1.0."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-pulse-speed
        (js/Math.min 5.0 (js/Math.max 0.1 n))))
    default-pulse-speed))

(defn parse-rest-rate
  "Parse rest-rate to float in [1.0, 10.0], default 4.0."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-rest-rate
        (js/Math.min 10.0 (js/Math.max 1.0 n))))
    default-rest-rate))

(defn parse-connection-distance
  "Parse connection-distance to float in [0.05, 0.5], default 0.15."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-connection-distance
        (js/Math.min 0.5 (js/Math.max 0.05 n))))
    default-connection-distance))

(defn parse-orb-size
  "Parse orb-size to float in [10, 200], default 40."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-orb-size
        (js/Math.min 200 (js/Math.max 10 n))))
    default-orb-size))

(defn parse-opacity
  "Parse opacity to float in [0.0, 1.0], default 0.8."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n)
        default-opacity
        (js/Math.min 1.0 (js/Math.max 0.0 n))))
    default-opacity))

(defn parse-interactive
  "Interactive defaults to true (present when absent)."
  [raw]
  (if (nil? raw)
    true
    (not= "false" (.toLowerCase (.trim raw)))))

;; ── Normalize ───────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map."
  [{:keys [orb-count-raw color-primary-raw color-secondary-raw
           color-background-raw pulse-speed-raw rest-rate-raw
           connection-distance-raw orb-size-raw opacity-raw
           interactive-raw]}]
  {:orb-count           (parse-orb-count orb-count-raw)
   :color-primary       (parse-color-primary color-primary-raw)
   :color-secondary     (parse-color-secondary color-secondary-raw)
   :color-background    (parse-color-background color-background-raw)
   :pulse-speed         (parse-pulse-speed pulse-speed-raw)
   :rest-rate           (parse-rest-rate rest-rate-raw)
   :connection-distance (parse-connection-distance connection-distance-raw)
   :orb-size            (parse-orb-size orb-size-raw)
   :opacity             (parse-opacity opacity-raw)
   :interactive?        (parse-interactive interactive-raw)})

;; ── Pure helpers ────────────────────────────────────────────────────────────
(defn lerp
  "Linear interpolation: moves current toward target by speed fraction."
  [current target speed]
  (+ current (* (- target current) speed)))

(defn init-orb-positions
  "Returns a JS array of n [x, y] pairs with random values in [0, 1]."
  [n]
  (let [out #js []]
    (dotimes [_ n]
      (.push out #js [(js/Math.random) (js/Math.random)]))
    out))

(defn init-orb-phases
  "Returns a JS array of n random phase offsets in [0, 2*PI]."
  [n]
  (let [out #js []
        two-pi (* 2.0 js/Math.PI)]
    (dotimes [_ n]
      (.push out (* (js/Math.random) two-pi)))
    out))

;; ── GLSL Shaders ────────────────────────────────────────────────────────────
(def vertex-shader-source
  "attribute vec2 a_position;
void main() {
    gl_Position = vec4(a_position, 0.0, 1.0);
}")

(def ^:private max-orbs 50)

(def fragment-shader-source
  (str
   "precision mediump float;
uniform vec2  u_resolution;
uniform float u_time;
uniform float u_activity;
uniform float u_pulse_speed;
uniform float u_rest_rate;
uniform int   u_orb_count;
uniform vec2  u_orb_positions[" max-orbs "];
uniform float u_orb_phases[" max-orbs "];
uniform float u_orb_size;
uniform float u_connection_dist;
uniform vec3  u_color_primary;
uniform vec3  u_color_secondary;
uniform vec3  u_color_background;
uniform float u_opacity;

float distToSegment(vec2 p, vec2 a, vec2 b) {
    vec2 pa = p - a;
    vec2 ba = b - a;
    float t = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
    return length(pa - ba * t);
}

void main() {
    vec2 uv = gl_FragCoord.xy / u_resolution;
    float aspect = u_resolution.x / u_resolution.y;

    // Auto-detect light vs dark background
    float bg_lum = dot(u_color_background, vec3(0.2126, 0.7152, 0.0722));
    float is_light = smoothstep(0.3, 0.6, bg_lum);

    // Pulse speed interpolates between rest and active rate
    float active_speed = u_pulse_speed * 3.0;
    float speed = mix(6.2831853 / u_rest_rate, active_speed, u_activity);

    // Start with background
    vec3 color = u_color_background;

    // Orb glow layer
    float radius = u_orb_size / min(u_resolution.x, u_resolution.y);
    float softness = 2.5;

    for (int i = 0; i < " max-orbs "; i++) {
        if (i >= u_orb_count) break;

        vec2 orbPos = u_orb_positions[i];
        vec2 diff = uv - orbPos;
        diff.x *= aspect;

        float dist = length(diff);
        float pulse = 0.5 + 0.5 * sin(u_time * speed + u_orb_phases[i]);
        float glow_strength = (0.4 + 0.6 * pulse) * (0.5 + 0.5 * u_activity + 0.5);
        float glow = exp(-dist * dist / (radius * radius * softness)) * glow_strength;

        // Alternate primary/secondary by index
        vec3 orb_color = mix(u_color_primary, u_color_secondary, step(1.0, mod(float(i), 2.0)));
        vec3 additive = color + orb_color * glow;
        vec3 multiply = color * mix(vec3(1.0), orb_color, glow);
        color = mix(additive, multiply, is_light);
    }

    // Connection line layer
    float line_width = 0.003;
    for (int i = 0; i < " max-orbs "; i++) {
        if (i >= u_orb_count) break;
        for (int j = 0; j < " max-orbs "; j++) {
            if (j >= u_orb_count) break;
            if (j <= i) continue;

            vec2 pi_pos = u_orb_positions[i];
            vec2 pj_pos = u_orb_positions[j];

            vec2 diff_ij = pi_pos - pj_pos;
            diff_ij.x *= aspect;
            float orb_dist = length(diff_ij);

            if (orb_dist < u_connection_dist) {
                vec2 uv_a = vec2(uv.x * aspect, uv.y);
                vec2 pa = vec2(pi_pos.x * aspect, pi_pos.y);
                vec2 pb = vec2(pj_pos.x * aspect, pj_pos.y);

                float d = distToSegment(uv_a, pa, pb);
                float fade = 1.0 - (orb_dist / u_connection_dist);
                float avg_phase = (u_orb_phases[i] + u_orb_phases[j]) * 0.5;
                float line_pulse = 0.3 + 0.7 * sin(u_time * speed + avg_phase);
                float line_glow = exp(-d * d / (line_width * line_width)) * fade * line_pulse;
                line_glow *= (0.3 + 0.7 * u_activity + 0.3);

                vec3 line_color = mix(u_color_primary, u_color_secondary, 0.5);
                float line_str = line_glow * 0.4;
                vec3 line_add = color + line_color * line_str;
                vec3 line_mul = color * mix(vec3(1.0), line_color, line_str);
                color = mix(line_add, line_mul, is_light);
            }
        }
    }

    gl_FragColor = vec4(color, u_opacity);
}"))
