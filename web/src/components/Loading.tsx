


import { MoonLoader } from 'react-spinners';

export function Loading() {
    return (
      <div className='flex justify-center items-center h-screen w-screen'>
          <MoonLoader
                  cssOverride={{}}
                  loading
                  size={30}
                  speedMultiplier={1}
          />
      </div>
    );
  }