# Vue 3 + Vite Frontend

## Architecture

```
frontend/
├── src/
│   ├── components/           # Reusable UI components
│   │   ├── common/          # Header, Sidebar, Footer, etc.
│   │   ├── contest/         # Contest-specific components
│   │   ├── editor/          # Code editor components
│   │   └── admin/           # Admin dashboard components
│   ├── pages/               # Page components (routed views)
│   ├── stores/              # Pinia state management
│   ├── composables/         # Reusable composition functions
│   ├── api/                 # API client and endpoints
│   ├── router/              # Vue Router configuration
│   ├── styles/              # Global CSS (Tailwind)
│   ├── utils/               # Helper functions
│   ├── App.vue              # Root component
│   └── main.js              # Entry point
├── public/                  # Static assets
├── index.html               # HTML template
├── vite.config.js           # Vite configuration
├── tailwind.config.js       # Tailwind CSS configuration
├── postcss.config.js        # PostCSS configuration
├── package.json             # Dependencies
└── README.md
```

## Key Features

### 1. **Pages**
- **Dashboard** - User statistics and contest overview
- **Contest Listing** - Browse all contests with filtering
- **Contest Detail** - View contest info, problems, and leaderboard
- **Code Editor** - Monaco editor with syntax highlighting
- **Leaderboards** - Global and contest-specific rankings
- **Admin Panel** - Contest and user management

### 2. **Components**
- **Header** - Navigation and dark mode toggle
- **Sidebar** - Main navigation menu
- **ContestCard** - Contest preview with join button
- **CodeEditor** - Full-featured Monaco-based editor
- **LeaderboardTable** - Responsive ranking table
- **AdminDashboard** - Admin stats and controls

### 3. **State Management** (Pinia)
- **User Store** - Authentication and user state
- **Contest Store** - Contest data and operations
- **Leaderboard Store** - Rankings and user positions

### 4. **Composables** (Reusable Logic)
- **useContests** - Contest fetching and filtering
- **useContest** - Single contest with questions
- **useExecution** - Code execution with output
- **useLeaderboard** - Leaderboard pagination
- **useUser** - User profile and stats

### 5. **API Integration**
- Axios client with interceptors
- Organized endpoint definitions
- Automatic token injection
- Request/response handling
- Error handling

### 6. **Styling**
- Tailwind CSS for utilities
- Dark mode support (class-based)
- Responsive grid layouts
- Custom component classes
- Animation utilities

### 7. **Dark Mode**
- Toggle in header
- Persisted in localStorage
- Applied via `dark` class on HTML
- Tailwind dark mode classes throughout

### 8. **Lazy Loading**
- Route-based code splitting
- Dynamic imports in Router
- Component lazy loading
- Image lazy loading ready

## Getting Started

```bash
# Install dependencies
npm install

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

## Configuration

### API Base URL
Update proxy in `vite.config.js`:
```javascript
'/api': 'http://your-backend-url'
```

### Backend Services
- **Auth Service** - Port 8081
- **User Service** - Port 8082
- **Contest Service** - Port 8083
- **Execution Service** - Port 8084
- **Leaderboard Service** - Port 8085
- **Notification Service** - Port 8085
- **Payment Service** - Port 8086

## Component Props & Events

### ContestCard
```vue
<ContestCard 
  :contest="contestObj"
  @joined="onContestJoined"
/>
```

### CodeEditor
```vue
<CodeEditor 
  ref="editor"
  :initialCode="code"
/>
// Access: editor.value.editor.getValue()
```

### LeaderboardTable
```vue
<LeaderboardTable 
  :leaderboard="entries"
  :totalQuestions="10"
/>
```

## API Endpoints Used

### Contests
- `GET /api/contests` - List all
- `GET /api/contests/{id}` - Get details
- `POST /api/contests` - Create (admin)
- `PUT /api/contests/{id}` - Update (admin)
- `POST /api/contests/{id}/register` - Join
- `GET /api/contests/{id}/questions` - Get problems

### Execution
- `POST /api/execution/run` - Run code
- `GET /api/execution/languages` - Available languages

### Leaderboard
- `GET /api/leaderboards/global` - Global rankings
- `GET /api/leaderboards/contests/{id}` - Contest rankings

### User
- `GET /api/users/me` - Current user
- `GET /api/users/me/stats` - User statistics

## Browser Support
- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)

## Performance Tips
1. Use `:key` binding in v-for loops
2. Lazy load images with `loading="lazy"`
3. Debounce search inputs
4. Pagination for large lists
5. Code splitting for routes

## Future Enhancements
- [ ] Real-time notifications via WebSocket
- [ ] Collaborative code editor
- [ ] Rich code submission history
- [ ] Testing framework integration
- [ ] PWA support
- [ ] Advanced analytics dashboard
