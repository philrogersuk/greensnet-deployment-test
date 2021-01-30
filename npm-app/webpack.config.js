module.exports = {
    entry: {
        MoreDetailsLink: './src/ui/sharedComponents/MoreDetailsLink.tsx'
    },
    output: {
        filename: '[name].js',
        path: __dirname + '/build/static/js',
        library: ['MoreDetailsLink', 'components'],
        libraryTarget: 'umd',
        umdNamedDefine: true,
    },

    mode: "production",

    // Enable sourcemaps for debugging webpack's output.
    devtool: "source-map",

    resolve: {
        // Add '.ts' and '.tsx' as resolvable extensions.
        extensions: [".ts", ".tsx", ".js", ".jsx"]
    },

    module: {
        rules: [
            {
                test: /\.ts(x?)$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: "ts-loader"
                    }
                ]
            },
            // All output '.js' files will have any sourcemaps re-processed by 'source-map-loader'.
            {
                enforce: "pre",
                test: /\.js$/,
                loader: "source-map-loader"
            }
        ]
    }
};