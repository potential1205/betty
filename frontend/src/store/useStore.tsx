interface Game {
    id: number;
    gameId: number;
    homeTeamId: number;
    awayTeamId: number;
    homeTeam: string;
    awayTeam: string;
    homeScore: number;
    awayScore: number;
    inning: number;
    status: string;
    schedule: any;
}