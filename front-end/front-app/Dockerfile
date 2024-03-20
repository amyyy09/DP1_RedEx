# Dockerfile
# preferred node version chosen here (LTS = 18.18 as of 10/10/23)
FROM node:18-alpine AS base

# Install dependencies only when needed
FROM base AS deps
# Check https://github.com/nodejs/docker-node/tree/b4117f9333da4138b03a546ec926ef50a31506c3#nodealpine to understand why libc6-compat might be needed.
RUN apk add --no-cache libc6-compat

# Create the directory on the node image
# where our Next.js app will live
RUN mkdir -p /src/app

# Set /src/app as the working directory in container
WORKDIR /src/app

# Copy package.json and package-lock.json
# to the /src/app working directory
COPY package*.json ./

# Install dependencies in /src/app
RUN npm install

# Copy the rest of our Next.js folder into /src/app
COPY . .

# Ensure port 3000 is accessible to our system
EXPOSE 3000

# Run dev, as we would via the command line
CMD npm run dev