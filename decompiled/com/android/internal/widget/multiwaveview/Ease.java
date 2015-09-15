package com.android.internal.widget.multiwaveview;

import android.animation.TimeInterpolator;

class Ease {
    private static final float DOMAIN = 1.0f;
    private static final float DURATION = 1.0f;
    private static final float START = 0.0f;

    static class Cubic {
        public static final TimeInterpolator easeIn;
        public static final TimeInterpolator easeInOut;
        public static final TimeInterpolator easeOut;

        Cubic() {
        }

        static {
            easeIn = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= Ease.DURATION;
                    return (((Ease.DURATION * input) * input) * input) + 0.0f;
                }
            };
            easeOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input = (input / Ease.DURATION) - Ease.DURATION;
                    return ((((input * input) * input) + Ease.DURATION) * Ease.DURATION) + 0.0f;
                }
            };
            easeInOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= 0.5f;
                    if (input < Ease.DURATION) {
                        return (((0.5f * input) * input) * input) + 0.0f;
                    }
                    input -= 2.0f;
                    return ((((input * input) * input) + 2.0f) * 0.5f) + 0.0f;
                }
            };
        }
    }

    static class Linear {
        public static final TimeInterpolator easeNone;

        Linear() {
        }

        static {
            easeNone = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    return input;
                }
            };
        }
    }

    static class Quad {
        public static final TimeInterpolator easeIn;
        public static final TimeInterpolator easeInOut;
        public static final TimeInterpolator easeOut;

        Quad() {
        }

        static {
            easeIn = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= Ease.DURATION;
                    return ((Ease.DURATION * input) * input) + 0.0f;
                }
            };
            easeOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= Ease.DURATION;
                    return ((-1.0f * input) * (input - 2.0f)) + 0.0f;
                }
            };
            easeInOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= 0.5f;
                    if (input < Ease.DURATION) {
                        return ((0.5f * input) * input) + 0.0f;
                    }
                    input -= Ease.DURATION;
                    return (-0.5f * (((input - 2.0f) * input) - Ease.DURATION)) + 0.0f;
                }
            };
        }
    }

    static class Quart {
        public static final TimeInterpolator easeIn;
        public static final TimeInterpolator easeInOut;
        public static final TimeInterpolator easeOut;

        Quart() {
        }

        static {
            easeIn = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= Ease.DURATION;
                    return ((((Ease.DURATION * input) * input) * input) * input) + 0.0f;
                }
            };
            easeOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input = (input / Ease.DURATION) - Ease.DURATION;
                    return (-1.0f * ((((input * input) * input) * input) - Ease.DURATION)) + 0.0f;
                }
            };
            easeInOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= 0.5f;
                    if (input < Ease.DURATION) {
                        return ((((0.5f * input) * input) * input) * input) + 0.0f;
                    }
                    input -= 2.0f;
                    return (-0.5f * ((((input * input) * input) * input) - 2.0f)) + 0.0f;
                }
            };
        }
    }

    static class Quint {
        public static final TimeInterpolator easeIn;
        public static final TimeInterpolator easeInOut;
        public static final TimeInterpolator easeOut;

        Quint() {
        }

        static {
            easeIn = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= Ease.DURATION;
                    return (((((Ease.DURATION * input) * input) * input) * input) * input) + 0.0f;
                }
            };
            easeOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input = (input / Ease.DURATION) - Ease.DURATION;
                    return ((((((input * input) * input) * input) * input) + Ease.DURATION) * Ease.DURATION) + 0.0f;
                }
            };
            easeInOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    input /= 0.5f;
                    if (input < Ease.DURATION) {
                        return (((((0.5f * input) * input) * input) * input) * input) + 0.0f;
                    }
                    input -= 2.0f;
                    return ((((((input * input) * input) * input) * input) + 2.0f) * 0.5f) + 0.0f;
                }
            };
        }
    }

    static class Sine {
        public static final TimeInterpolator easeIn;
        public static final TimeInterpolator easeInOut;
        public static final TimeInterpolator easeOut;

        Sine() {
        }

        static {
            easeIn = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    return ((-1.0f * ((float) Math.cos(((double) (input / Ease.DURATION)) * 1.5707963267948966d))) + Ease.DURATION) + 0.0f;
                }
            };
            easeOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    return (((float) Math.sin(((double) (input / Ease.DURATION)) * 1.5707963267948966d)) * Ease.DURATION) + 0.0f;
                }
            };
            easeInOut = new TimeInterpolator() {
                public float getInterpolation(float input) {
                    return (-0.5f * (((float) Math.cos((3.141592653589793d * ((double) input)) / 1.0d)) - Ease.DURATION)) + 0.0f;
                }
            };
        }
    }

    Ease() {
    }
}
