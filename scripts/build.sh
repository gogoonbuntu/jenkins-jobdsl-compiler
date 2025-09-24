#!/usr/bin/env bash
set -Eeuo pipefail
IFS=$'\n\t'

echo "[build.sh] Starting build for: ${NAME:-world}"

# Special characters test line
echo "SPECIALS: ! @ # $ % ^ & * ( ) - _ = + [ ] { } ; : ' \" | , . < > / ? ` ~"

# JSON payload with quotes and braces
payload='{"message":"Hello, \"'$NAME'\" & <world>", "arr":[1,2,3], "nested":{"k":"v$NAME"}}'
echo "JSON: ${payload}"

# sed with capture groups and brackets
echo "FooBarBaz" | sed -E 's/(Foo)(Bar)(Baz)/[\1]-{\2}-(\3)/'

# awk script with braces, dollars, and quotes
printf '%s\n' 'a b c' | awk '{ print "AWK:" $1, $2, $3 }'

# here-doc with lots of specials and backslashes
cat <<'EOS'
HEREDOC: ; | & < > ( ) [ ] { } $ ` ' " \
$(date) ${NOT_EXPANDED}
EOS

# Find/exec with braces and semicolons
mkdir -p tmp && touch tmp/{a,b,c}.txt
find tmp -type f -name '*.txt' -exec bash -lc 'echo FILE:{}; echo CONTENT; cat {} 2>/dev/null || true' \; || true

echo "[build.sh] Done."

