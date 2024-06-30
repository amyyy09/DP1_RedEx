// utils.ts
export function timeToMinutes(time: string): number {
  const [hours, minutes] = time.split(":").map(Number);
  return hours * 60 + minutes;
}

export function minutesToTime(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours.toString().padStart(2, "0")}:${mins
    .toString()
    .padStart(2, "0")}`;
}

export function arrayToTime(time: number[]): Date {
  if (!time || time.length < 5) {
    console.error("Invalid time array:", time);
    return new Date(); // or handle it in a way that fits your use case
  }
  return new Date(time[0], time[1] - 1, time[2], time[3], time[4]);
}
