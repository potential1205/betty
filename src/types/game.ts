export interface Game {
    id: string;
    homeTeam: string;
    awayTeam: string;
    homeScore: number;
    awayScore: number;
    status: 'LIVE' | 'ENDED' | 'CANCELED' | 'SCHEDULED';
    schedule: {
      stadium: string;
      startTime: string;
    };
  } 