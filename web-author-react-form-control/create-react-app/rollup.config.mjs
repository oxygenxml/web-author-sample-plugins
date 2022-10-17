import babel from "@rollup/plugin-babel";
import {nodeResolve} from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import { terser } from 'rollup-plugin-terser';
import replace from "@rollup/plugin-replace";

export default [
  {
    input: "src/plugin.js",
    output: [
      {
        file: '../web/bundle.js',
        format: "iife",
        sourcemap: true,
      }
    ],
    plugins: [
      replace({
        'process.env.NODE_ENV': JSON.stringify('production'),
        preventAssignment: true
      }),
      babel({
        presets: ["@babel/preset-react"],
        babelHelpers: 'runtime',
        plugins: ['@babel/transform-runtime'],
      }),
      nodeResolve(),
      commonjs(),
      terser()
    ],
  }
];
