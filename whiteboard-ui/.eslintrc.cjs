module.exports = {
    root: true,
    parser: '@typescript-eslint/parser',
    plugins: ['@typescript-eslint', 'react-refresh'],
    extends: [
        'eslint:recommended',
        'plugin:@typescript-eslint/recommended',
        'plugin:react/recommended',
        'plugin:react-hooks/recommended',
        'prettier'
    ],
    settings: { react: { version: '18.0' } },
    env: { browser: true, es2021: true, node: true },
    rules: { 'react/react-in-jsx-scope': 'off', 'react/prop-types': 'off' }
}
