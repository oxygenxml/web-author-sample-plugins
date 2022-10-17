import babel from "@rollup/plugin-babel";
import {nodeResolve} from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import { terser } from 'rollup-plugin-terser';

export default [
  {
    input: "src/plugin.js",
    output: [
      {
        file: 'dist/bundle.js',
        format: "iife",
        sourcemap: true,
      }
    ],
    plugins: [
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
