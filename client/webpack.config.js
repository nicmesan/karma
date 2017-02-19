var path = require('path');
var webpack = require('webpack');
var npmPath = path.resolve(__dirname, './node_modules');

module.exports = {
    entry: "./src/index.js",
    output: {
        path: __dirname + '/static',
        publicPath: "/static/",
        filename: "bundle.js",
    },
    module: {
        loaders: [
            {
                test: /\.css$/,
                loader: "style!css",
            },

            {
                test: /\.js?$/,
                loaders: ['babel'],
                exclude: npmPath,
            },
        ]
    }
};