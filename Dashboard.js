import { useEffect, useState } from "react";

export default function Dashboard() {
  const [data, setData] = useState([]);

  useEffect(() => {
    setInterval(fetchData, 5000);
  }, []);

  async function fetchData() {
    const res = await fetch(
      "https://akqbbsnangcxbdktmruk.supabase.co/rest/v1/locations",
      {
        headers: {
          apikey: "ISI_API_KEY_ANDA"
        }
      }
    );
    const json = await res.json();
    setData(json);
  }

  return (
    <div>
      <h1>Tracking Kurir</h1>
      {data.map((d, i) => (
        <div key={i}>
          {d.lat} - {d.lng} ({d.status})
        </div>
      ))}
    </div>
  );
}
