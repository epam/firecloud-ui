window.jQuery = require('jquery');
window.$ = window.jQuery;
require('jquery-ui/ui/core');
require('jquery-ui/ui/widgets/draggable');
exports.CodeMirror = require('codemirror');
require('codemirror/mode/clojure/clojure');
exports.MarkdownIt = require('markdown-it');
exports.Pipeline = require('pipeline-builder');
require('./js/timingDiagram');
require('./js/igvPatch');
require('any-resize-event');
require('what-input');
require('foundation-sites/js/foundation.core');
require('foundation-sites/js/foundation.util.mediaQuery');
require('foundation-sites/js/foundation.util.box');
require('foundation-sites/js/foundation.util.keyboard');
require('foundation-sites/js/foundation.util.triggers');
require('foundation-sites/js/foundation.dropdown');
require('foundation-sites/js/foundation.magellan');
require('foundation-sites/js/foundation.sticky');
require('foundation-sites/js/foundation.tooltip');
require('select2');
require('./js/select2MonkeyPatch');

require('jquery-ui/themes/base/core.css');
require('jquery-ui/themes/base/theme.css');
require('jquery-ui/themes/base/draggable.css');
require('codemirror/lib/codemirror.css');
require('codemirror/theme/icecoder.css');
require('github-markdown-css/github-markdown.css');
require('font-awesome/css/font-awesome.css');
require('pipeline-builder/dist/pipeline.css');
require('./styles/pipeline.scss');
require('./styles/codemirror.scss');
require('./styles/markdown.css');
require('./styles/foundation.scss');
require('./styles/select2.scss');
require('./styles/react-shims.scss');
