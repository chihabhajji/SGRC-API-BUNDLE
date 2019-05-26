import { Data } from './Data';
export class YearlySummary {
  constructor (
    public september : Data ,
    public october: Data,
    public november: Data,
    public december: Data,
    public march: Data,
    public may: Data,
    public june: Data,
    public july: Data,
    public april: Data,
    public february: Data,
    public august: Data,
    public january: Data
  ) {}
}
